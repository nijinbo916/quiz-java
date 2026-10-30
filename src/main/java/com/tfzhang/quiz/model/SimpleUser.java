package com.tfzhang.quiz.model;

/**
 * 简单参数实体类：用来演示「实体参数请求」。
 * 浏览器传 name、age 两个参数时，Spring 会自动把它们塞进这个对象。
 */
public class SimpleUser {

    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "SimpleUser{name='" + name + "', age=" + age + '}';
    }
}
