package com.tfzhang.quiz.service;

import com.tfzhang.quiz.model.PageBean;
import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.model.User;

import java.util.List;

/**
 * 用户业务接口（三层架构里的 Service 层）。
 *
 * 为什么要单独抽一层：Controller 只负责「接收请求、返回结果」，
 * 真正的业务规则（判空、密码一致性、用户名查重、密码加密）都放在 Service 里。
 * 接口和实现分离，是为了以后换实现（比如改成操作 Redis）时 Controller 一行都不用改。
 */
public interface UserService {

    /** 注册新用户：校验用户名密码 -> 查重 -> MD5 加盐加密 -> 入库 */
    Result addUser(String username, String password, String checkpassword);

    /** 按 id 逻辑删除，返回 true 表示确实删掉了一条 */
    boolean deleteUserById(Long id);

    /** 按用户名逻辑删除 */
    boolean deleteUserByName(String username);

    /** 物理删除（老师留的作业）：只允许删已经被逻辑删除的数据 */
    boolean hardDeleteById(Long id);

    /** 分页查询：page 从 1 开始，pageSize 每页条数 */
    PageBean page(Integer page, Integer pageSize);

    /** 按用户名模糊查询，结果里的密码会打码 */
    List<User> findByName(String keyword);

    /** 查询全部未删除用户，结果里的密码会打码 */
    List<User> list();

    /** 按 id 查询单个用户，密码打码 */
    User getById(Long id);
}
