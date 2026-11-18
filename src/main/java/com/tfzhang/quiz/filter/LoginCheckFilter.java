package com.tfzhang.quiz.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT 登录校验过滤器（对应视频 10-5 / 10-6）。
 *
 * 规则（和老师笔记 2.7 保持一致）：
 * 1、url 里带 login 或 register 的一律放行——登录、注册本身不能再要求已经登录；
 * 2、其余接口必须带 token 请求头，解析失败就拦下，返回 code=0 的 JSON；
 * 3、管理端专用的接口（加删用户、重置密码、题目增删改等）额外要求 token 里的
 *    userRole 是管理员（1），普通用户的 token 拿不到这些接口。
 *
 * 前端带 token 的方式：请求头名为 token（老师笔记里 quiz.html 就是这么带的）。
 */
@Component
public class LoginCheckFilter implements Filter {

    /** Jackson 的 JSON 转换器，用来把 Result 对象写成响应体 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = request.getRequestURL().toString();

        // 1、登录和注册放行（笔记 2.7 步骤3 的原话：对 register 接口放行）
        if (url.contains("login") || url.contains("register")) {
            chain.doFilter(request, response);
            return;
        }

        // 2、其余接口都要先验 token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            writeJson(response, Result.error("未登录或token缺失"));
            return;
        }

        Claims claims;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            writeJson(response, Result.error("token无效或已过期"));
            return;
        }

        // 3、管理端接口额外要求管理员角色
        if (isAdminOnlyApi(url) && !Integer.valueOf(1).equals(claims.get("userRole", Integer.class))) {
            writeJson(response, Result.error("权限不足：该操作仅管理员可用"));
            return;
        }

        // 4、校验通过，把用户信息放回请求里，Controller 里需要时可以取
        request.setAttribute("userId", claims.get("id"));
        request.setAttribute("username", claims.get("username"));
        chain.doFilter(request, response);
    }

    /** 管理端专用接口清单：命中任意关键词就必须是管理员 */
    private boolean isAdminOnlyApi(String url) {
        return url.contains("addUser")
                || url.contains("deleteById") || url.contains("deleteByName") || url.contains("hardDelete")
                || url.contains("resetPassword")
                || url.contains("addQuestion") || url.contains("updateQuestion") || url.contains("delQuestion")
                || url.contains("/users") || url.contains("findUser")
                || url.contains("/questions") || url.contains("findQuestion");
    }

    /** 把 Result 写成 UTF-8 的 JSON 响应 */
    private void writeJson(HttpServletResponse response, Result result) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        byte[] body = objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8);
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
    }
}
