package com.tfzhang.quiz;

import com.tfzhang.quiz.mapper.UserMapper;
import com.tfzhang.quiz.model.PageBean;
import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.model.User;
import com.tfzhang.quiz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * MyBatis / Service 测试类。
 *
 * 类上加了 @Transactional：每个测试方法跑完自动回滚，测试造的假数据不会留在数据库里。
 */
@SpringBootTest
@Transactional
class QuizApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /** 查询全部用户：能把数据库里的用户打印出来就说明 MyBatis 打通了 */
    @Test
    public void testListUser() {
        List<User> userList = userMapper.list();
        System.out.println("查询到 " + userList.size() + " 位用户：");
        userList.forEach(System.out::println);
    }

    /** 新增用户 + 主键回填 + 按 id 查回来（视频 9-4 的 Mapper 部分） */
    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUserName("测试用户_mvn");
        user.setUserPassword("123456");
        user.setIsDelete(0);
        user.setUserRole(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        int rows = userMapper.saveUser(user);
        System.out.println("插入影响行数 = " + rows + "，回填的主键 id = " + user.getId());

        User saved = userMapper.getById(user.getId());
        System.out.println("按 id 查回来：" + saved);
    }

    /** 用户名查重（视频 9-5 的练习）：已存在返回 1，不存在返回 0 */
    @Test
    public void testExistsByName() {
        User user = new User();
        user.setUserName("checkdup");
        user.setUserPassword("123456");
        user.setIsDelete(0);
        user.setUserRole(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.saveUser(user);

        System.out.println("插入后查重 checkdup = " + userMapper.existsByName("checkdup") + "（应为 1）");
        System.out.println("查重 notexists999 = " + userMapper.existsByName("notexists999") + "（应为 0）");
    }

    /** 逻辑删除：按 id 和按用户名各试一次（视频 9-6） */
    @Test
    public void testDeleteUser() {
        User byId = buildUser("del_by_id");
        userMapper.saveUser(byId);
        System.out.println("按 id 删除影响行数 = " + userMapper.deleteUserById(byId.getId()) + "（应为 1）");
        System.out.println("删除后还能查到吗：" + userMapper.getById(byId.getId()) + "（应为 null）");

        User byName = buildUser("del_by_name");
        userMapper.saveUser(byName);
        System.out.println("按用户名删除影响行数 = " + userMapper.deleteUserByUsername("del_by_name") + "（应为 1）");
        System.out.println("删除后还能查到吗：" + userMapper.getById(byName.getId()) + "（应为 null）");
    }

    /** 分页查询（视频 9-7）：总数、第一页、第二页 */
    @Test
    public void testPage() {
        int total = userMapper.count();
        System.out.println("未删除用户总数 = " + total);

        List<User> page1 = userMapper.page(0, 5);
        System.out.println("第 1 页 " + page1.size() + " 条：");
        page1.forEach(u -> System.out.println("   " + u.getId() + " " + u.getUserName()));

        List<User> page2 = userMapper.page(5, 5);
        System.out.println("第 2 页 " + page2.size() + " 条：");
        page2.forEach(u -> System.out.println("   " + u.getId() + " " + u.getUserName()));
    }

    /** 按用户名模糊查询（视频 9-7 第二部分） */
    @Test
    public void testFindByName() {
        List<User> users = userMapper.findByName("小");
        System.out.println("名字里带「小」的用户共 " + users.size() + " 位：");
        users.forEach(u -> System.out.println("   " + u.getId() + " " + u.getUserName()));
    }

    /** Service 层注册：密码加密、用户名查重、各种非法输入 */
    @Test
    public void testServiceAddUser() {
        System.out.println("== 空用户名");
        System.out.println("  " + msg(userService.addUser("", "12345678", "12345678")));

        System.out.println("== 两次密码不一致");
        System.out.println("  " + msg(userService.addUser("tom001", "12345678", "87654321")));

        System.out.println("== 用户名带特殊字符");
        System.out.println("  " + msg(userService.addUser("tom#001", "12345678", "12345678")));

        System.out.println("== 正常注册");
        Result ok = userService.addUser("tom001", "12345678", "12345678");
        System.out.println("  " + msg(ok));

        System.out.println("== 同一个用户名再注册一次（应被查重拦下）");
        System.out.println("  " + msg(userService.addUser("tom001", "12345678", "12345678")));

        System.out.println("== 数据库里存的是密文吗");
        Integer exists = userMapper.existsByName("tom001");
        System.out.println("  tom001 是否已入库：" + exists);
        List<User> found = userMapper.findByName("tom001");
        if (!found.isEmpty()) {
            System.out.println("  库里的密码字段 = " + found.get(0).getUserPassword() + "（应为 32 位 MD5）");
        }
    }

    /** Service 层分页：返回 PageBean（total + 当前页数据，密码已打码） */
    @Test
    public void testServicePage() {
        PageBean bean = userService.page(1, 5);
        System.out.println("总数 = " + bean.getTotal() + "，当前页条数 = " + bean.getRows().size());
        bean.getRows().forEach(u -> System.out.println("   " + u.getId() + " " + u.getUserName() + " / " + u.getUserPassword()));
    }

    /** 物理删除（老师留的作业）：先逻辑删除，再彻底删掉 */
    @Test
    public void testHardDelete() {
        User user = buildUser("hard_del");
        userMapper.saveUser(user);

        System.out.println("未逻辑删除就直接物理删：" + userMapper.hardDeleteById(user.getId()) + "（应为 0，被保护拦住）");
        userMapper.deleteUserById(user.getId());
        System.out.println("逻辑删除后再物理删：" + userMapper.hardDeleteById(user.getId()) + "（应为 1）");
    }

    private User buildUser(String name) {
        User user = new User();
        user.setUserName(name);
        user.setUserPassword("123456");
        user.setIsDelete(0);
        user.setUserRole(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        return user;
    }

    private String msg(Result result) {
        return "code=" + result.getCode() + " msg=" + result.getMsg();
    }
}
