package com.tfzhang.quiz.controller;

import com.tfzhang.quiz.model.PageBean;
import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.model.User;
import com.tfzhang.quiz.service.UserService;
import com.tfzhang.quiz.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户接口层。
 *
 * 三层架构改造后，这里只做两件事：接收参数、把结果包成 Result 返回；
 * 判断和业务逻辑全在 UserService 里。
 *
 * @CrossOrigin 是给本地前后端联调用的（Vue 开发服务器 8081 调 8080 属于跨域）；
 * 部署到 Ubuntu 后由 Nginx 反向代理转发，属于同源请求，不再依赖它。
 */
@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录（10-4 登录的前端和后端 / 10-5 JWT 令牌验证）。
     *
     * 请求体 JSON：{"username":"admin","password":"123456","userrole":"1"}
     * userrole 传 "1" 表示这次登录来自管理端前端：
     * 只有管理员角色能通过，普通用户一律回 msg=invalid account（笔记 2.8 完善第 1 条）。
     * 用户端登录不传 userrole，普通用户和管理员都可以。
     *
     * 成功时 data 是 JWT token，前端存到 localStorage，之后每个请求带上 token 请求头。
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (StringUtils.isAnyBlank(username, password)) {
            return Result.error("用户名或密码为空");
        }

        User userResult = userService.login(username, password);
        if (userResult == null) {
            return Result.error("用户登录失败：用户名或密码错误");
        }

        // 管理端登录入口：普通用户不允许进管理端
        if ("1".equals(loginData.get("userrole")) && !Integer.valueOf(1).equals(userResult.getUserRole())) {
            return Result.error("invalid account");
        }

        Claims claims = Jwts.claims();
        claims.put("id", userResult.getId());
        claims.put("username", userResult.getUserName());
        claims.put("userRole", userResult.getUserRole());

        String token = JwtUtil.generateTokenWithClaims(claims);
        Result result = Result.success("用户登录成功");
        result.setData(token);
        return result;
    }

    /**
     * 注册新用户——用户端前端专用（笔记 2.7 步骤1）。
     * 请求体 JSON：{"username":"tom","password":"123456","checkpassword":"123456"}
     * 角色固定为普通用户（userRole=0），注册完去用户端登录页。
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> registerData) {
        String username = registerData.get("username");
        String password = registerData.get("password");
        String checkpassword = registerData.get("checkpassword");

        if (StringUtils.isAnyBlank(username, password, checkpassword)) {
            return Result.error("用户名或密码为空");
        }
        return userService.addUser(username, password, checkpassword);
    }

    /**
     * 管理端添加用户（笔记 2.7 步骤1）。
     * 请求体 JSON：{"username":"tom","password":"123456","checkpassword":"123456","userrole":"1"}
     * userrole 由管理端前端表单传入："0" 普通用户、"1" 管理员——对应验收条件 3.2 第 1 条的角色选项。
     * 此接口在过滤器里要求管理员 token。
     */
    @PostMapping("/addUser")
    public Result addUser(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String password = userData.get("password");
        String checkpassword = userData.get("checkpassword");
        String role = userData.get("userrole");

        if (StringUtils.isAnyBlank(username, password, checkpassword, role)) {
            return Result.error("用户名、密码或角色为空");
        }
        return userService.saveUser(username, password, checkpassword, role);
    }

    /**
     * 重置用户密码（验收条件 3.2 第 4 条）：初始密码固定为 123456。
     * 请求示例：GET /resetPassword?id=3（需管理员 token）
     */
    @GetMapping("/resetPassword")
    public Result resetPassword(@RequestParam Long id) {
        boolean success = userService.resetPassword(id);
        if (success) {
            return Result.successMsg("密码已重置为 123456");
        }
        return Result.error("重置失败：用户不存在或已删除");
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
