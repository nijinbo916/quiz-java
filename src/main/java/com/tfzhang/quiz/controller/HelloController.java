package com.tfzhang.quiz.controller;

import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.model.SimpleUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 老师笔记（9-3 Controller 测试）里的四个需求。
 *
 * @RestController：表示这个类是请求处理类，方法返回值直接写回浏览器（不再找页面）
 */
@RestController
public class HelloController {

    /** 需求1：无参数请求 —— 浏览器访问 /hello，返回 Hello World */
    @RequestMapping("/hello")
    public String hello() {
        System.out.println("收到 /hello 请求");
        return "Hello World";
    }

    /** 需求2：简单参数请求 —— /simpleParam?name=tom&age=10 */
    @RequestMapping("/simpleParam")
    public String getParam(String name, Integer age) {
        System.out.println("收到 /simpleParam 请求：name=" + name + ", age=" + age);
        return "ok";
    }

    /** 需求3：实体参数请求 —— 参数多的时候封装成对象接收 */
    @RequestMapping("/simpleUser")
    public String getUser(SimpleUser user) {
        System.out.println("收到 /simpleUser 请求：" + user);
        return "ok";
    }

    /** 需求4：统一响应结果（GET 版） */
    @GetMapping("/result/simpleUser")
    public Result getUserResult(SimpleUser user) {
        return Result.success(user);
    }

    /** 需求4：统一响应结果（POST 表单版），用来对比 GET / POST 都能收到参数 */
    @PostMapping("/result/simpleUser")
    public Result postUserResult(SimpleUser user) {
        return Result.success(user);
    }
}
