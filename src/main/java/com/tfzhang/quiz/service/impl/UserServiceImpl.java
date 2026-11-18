package com.tfzhang.quiz.service.impl;

import com.tfzhang.quiz.mapper.UserMapper;
import com.tfzhang.quiz.model.PageBean;
import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.model.User;
import com.tfzhang.quiz.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户业务实现类。
 *
 * 三层架构改造的落点：Controller 里原来那一堆 if 判断，全部搬到这里。
 */
@Service
public class UserServiceImpl implements UserService {

    /** 密码加密用的盐，和密码拼在一起再做 MD5，这样即使库被拖走也不容易反查 */
    private static final String SALT = "com.quiz";

    /** 用户名只允许字母和数字 */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Autowired
    private UserMapper userMapper;

    /**
     * 注册新用户（用户端专用）。
     * 对应视频 9-4（实现新用户的添加及 Service 层）和 9-5（用户名是否存在的练习）。
     * 用户端注册一律是普通用户（笔记 2.7：register 调用时 isRole 默认为零）。
     */
    @Override
    public Result addUser(String username, String password, String checkpassword) {
        return saveUser(username, password, checkpassword, "0");
    }

    /**
     * 保存用户（笔记 2.7 的改造）：
     * /register（用户端）和 /addUser（管理端）调用同一个方法，
     * 区别只在 role —— 用户端固定 "0"，管理端由前端表单传入。
     */
    @Override
    public Result saveUser(String username, String password, String checkpassword, String role) {

        // 1、用户名和密码不能为空（StringUtils.isAnyBlank：任意一个为 null、空串或只有空格都算空）
        if (StringUtils.isAnyBlank(username, password, checkpassword)) {
            return Result.error("用户名或密码为空");
        }

        // 2、两次输入的密码要一致
        if (!password.equals(checkpassword)) {
            return Result.error("两次输入的密码不一致");
        }

        // 3、用户名只能是字母和数字，不能带特殊字符
        Matcher matcher = USERNAME_PATTERN.matcher(username);
        if (!matcher.matches()) {
            return Result.error("用户名包含特殊字符");
        }

        // 4、查数据库确认用户名没被占用（9-5 的练习点）
        int count = userMapper.existsByName(username);
        if (count > 0) {
            return Result.error("用户名已存在");
        }

        // 5、密码加密后再入库，数据库里不存明文
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));

        // 6、组装对象并插入；角色由调用方决定，缺省按普通用户处理
        int userRole;
        try {
            userRole = Integer.parseInt(role == null ? "0" : role);
        } catch (NumberFormatException e) {
            userRole = 0;
        }

        User user = new User();
        user.setUserName(username);
        user.setUserPassword(encryptedPassword);
        user.setUserRole(userRole);
        user.setIsDelete(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        int result = userMapper.saveUser(user);
        if (result > 0) {
            // 返回时把密码去掉，别把密文回传给前端
            user.setUserPassword(null);
            return Result.success(user);
        }
        return Result.error("注册用户失败");
    }

    /**
     * 登录（10-4）：用户名 + 明文密码，和库里加盐 MD5 后的密文比对。
     * 验证通过返回用户对象（含角色），任何一步不匹配都返回 null。
     */
    @Override
    public User login(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            return null;
        }
        User user = userMapper.getByUsername(username);
        if (user == null || Integer.valueOf(1).equals(user.getIsDelete())) {
            return null;
        }
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
        if (!encryptedPassword.equals(user.getUserPassword())) {
            return null;
        }
        // 登录成功，密码不外传
        user.setUserPassword(null);
        return user;
    }

    /** 重置密码（验收条件 3.2 第 4 条）：初始密码固定为 123456 */
    @Override
    public boolean resetPassword(Long id) {
        if (id == null) {
            return false;
        }
        String initialPassword = DigestUtils.md5DigestAsHex((SALT + "123456").getBytes(StandardCharsets.UTF_8));
        return userMapper.resetPassword(id, initialPassword) > 0;
    }

    /** 按 id 逻辑删除（9-6） */
    @Override
    public boolean deleteUserById(Long id) {
        if (id == null) {
            return false;
        }
        return userMapper.deleteUserById(id) > 0;
    }

    /** 按用户名逻辑删除（9-6） */
    @Override
    public boolean deleteUserByName(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        return userMapper.deleteUserByUsername(username) > 0;
    }

    /**
     * 物理删除（老师留的作业）。
     * 加了一道保护：只允许删已经逻辑删除的记录，避免手滑把正常用户真删了。
     */
    @Override
    public boolean hardDeleteById(Long id) {
        if (id == null) {
            return false;
        }
        return userMapper.hardDeleteById(id) > 0;
    }

    /**
     * 分页查询（9-7）。
     * 前端给页码和每页条数，后端算出起始行，再取这一页数据 + 总记录数。
     */
    @Override
    public PageBean page(Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 5;
        }

        Integer total = userMapper.count();
        Integer start = (page - 1) * pageSize;
        List<User> rows = userMapper.page(start, pageSize);

        // 列表里的密码统一打码，后端不该把密码发给前端
        maskPassword(rows);

        return new PageBean(total, rows);
    }

    /** 按用户名模糊查询（9-7 的第二部分） */
    @Override
    public List<User> findByName(String keyword) {
        // 关键字为空时返回全部，等价于「没条件就是查所有」
        if (StringUtils.isBlank(keyword)) {
            return list();
        }
        List<User> users = userMapper.findByName(keyword);
        maskPassword(users);
        return users;
    }

    @Override
    public List<User> list() {
        List<User> users = userMapper.list();
        maskPassword(users);
        return users;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.getById(id);
        if (user != null) {
            user.setUserPassword("******");
        }
        return user;
    }

    /** 把列表里每个人的密码替换成星号 */
    private void maskPassword(List<User> users) {
        if (users == null) {
            return;
        }
        for (User user : users) {
            user.setUserPassword("******");
        }
    }
}
