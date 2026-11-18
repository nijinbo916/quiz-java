package com.tfzhang.quiz.exception;

import com.tfzhang.quiz.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（对应笔记 2.8「前后端的完善」第 3 条）。
 *
 * mapper -> service -> controller 一路往上抛的异常，最后都落到这里，
 * 不会再把堆栈直接甩给前端，同时用日志把异常信息记下来方便排查。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        log.error("发生异常:", ex);
        return Result.error("操作失败，请联系管理员！");
    }
}
