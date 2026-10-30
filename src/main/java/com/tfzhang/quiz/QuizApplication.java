package com.tfzhang.quiz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Quiz 后端启动类
 *
 * 第六次课（10-23）目标：
 * 1. Spring Boot 项目初始化（本文件）
 * 2. MyBatis 访问 MySQL（UserMapper + UserMapperTest）
 * 3. Controller 接收浏览器请求（HelloController / UserController）
 */
@SpringBootApplication
@MapperScan("com.tfzhang.quiz.mapper")
public class QuizApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
        System.out.println("Quiz 后端已启动：http://localhost:8080/hello");
    }
}
