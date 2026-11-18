package com.tfzhang.quiz.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类（对应视频 10-5：完成 JWT 令牌的登录验证功能）。
 *
 * 职责就两个：
 * 1、登录成功后，把用户信息塞进 claims 生成一个带过期时间的 token；
 * 2、收到请求时，把前端带来的 token 解开，取回里面的用户信息。
 *
 * HMAC-SHA256 要求密钥长度不少于 32 字节，这里给一串足够长的固定字符串。
 * 单机练手项目，密钥直接写死；生产环境应该放到配置文件或环境变量里。
 */
public class JwtUtil {

    /** 签名密钥（HS256 要求至少 32 字节） */
    private static final String SECRET = "quiz-jwt-secret-com-tfzhang-2026-0987654321";

    /** token 有效期：24 小时（单位毫秒） */
    private static final long EXPIRE = 24 * 60 * 60 * 1000L;

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 用给定的 claims 生成 token。
     * 调用方先把 id、username、userRole 塞进 Claims，再交给这里签名。
     */
    public static String generateTokenWithClaims(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 token，失败（过期、被篡改、格式错误）直接抛异常，由调用方捕获。
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
