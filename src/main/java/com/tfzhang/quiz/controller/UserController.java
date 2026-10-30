package com.tfzhang.quiz.controller;

import com.tfzhang.quiz.model.PageBean;
import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.model.User;
import com.tfzhang.quiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户接口层。
 *
 * 三层架构改造后，这里只做两件事：接收参数、把结果包成 Result 返回；
 * 判断和业务逻辑全在 UserService 里。
 *
 * @CrossOrigin 是给下节课前后端联调用的：Vue 开发服务器跑在 8081 端口，请求 8080 属于跨域，
 * 加上这个注解浏览器才不会拦截。
 */
@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册新用户（9-4 / 9-5）。
     * 请求示例：/register?username=tom001&password=12345678&checkpassword=12345678
     */
    @GetMapping("/register")
    public Result register(@RequestParam(required = false) String username,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) String checkpassword) {
        return userService.addUser(username, password, checkpassword);
    }

    /**
     * 分页查询用户（9-7）。
     * 请求示例：/users?page=1&pageSize=5
     */
    @GetMapping("/users")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "5") Integer pageSize) {
        PageBean pageBean = userService.page(page, pageSize);
        return Result.success(pageBean);
    }

    /** 查询全部用户（不分页，方便调试） */
    @GetMapping("/users/all")
    public Result list() {
        return Result.success(userService.list());
    }

    /** 按 id 查单个用户 */
    @GetMapping("/users/{id}")
    public Result getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在或已删除");
        }
        return Result.success(user);
    }

    /**
     * 按用户名模糊查询（9-7）。
     * 请求示例：/findUser?keyword=小
     */
    @GetMapping("/findUser")
    public Result findUser(@RequestParam(required = false) String keyword) {
        List<User> users = userService.findByName(keyword);
        return Result.success(users);
    }

    /** 按 id 逻辑删除（9-6）：/deleteById?id=12 */
    @GetMapping("/deleteById")
    public Result deleteById(@RequestParam Long id) {
        boolean success = userService.deleteUserById(id);
        if (success) {
            return Result.success("用户已删除");
        }
        return Result.error("用户不存在或已被删除");
    }

    /** 按用户名逻辑删除（9-6）：/deleteByName?username=tom001 */
    @GetMapping("/deleteByName")
    public Result deleteByName(@RequestParam String username) {
        boolean success = userService.deleteUserByName(username);
        if (success) {
            return Result.success("用户已删除");
        }
        return Result.error("用户不存在或已被删除");
    }

    /** 物理删除（老师留的作业）：只能删已经逻辑删除过的数据 */
    @DeleteMapping("/hardDelete/{id}")
    public Result hardDelete(@PathVariable Long id) {
        boolean success = userService.hardDeleteById(id);
        if (success) {
            return Result.success("已从数据库中彻底删除");
        }
        return Result.error("删除失败：该用户不存在，或尚未经过逻辑删除");
    }
}
