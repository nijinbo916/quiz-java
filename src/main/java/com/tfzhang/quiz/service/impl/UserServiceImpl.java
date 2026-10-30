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
     * 注册新用户。
     * 对应视频 9-4（实现新用户的添加及 Service 层）和 9-5（用户名是否存在的练习）。
     */
    @Override
    public Result addUser(String username, String password, String checkpassword) {

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

        // 6、组装对象并插入
        User user = new User();
        user.setUserName(username);
        user.setUserPassword(encryptedPassword);
        user.setUserRole(0);   // 注册的一律是普通用户
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
