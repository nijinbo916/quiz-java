package com.tfzhang.quiz.model;

/**
 * 统一响应结果类（老师笔记「需求4：统一响应结果」）。
 * code：1 成功，0 失败；msg：提示信息；data：真正的数据。
 */
public class Result {

    private Integer code;
    private String msg;
    private Object data;

    public Result() {
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success() {
        return new Result(1, "success", null);
    }

    public static Result success(Object data) {
        return new Result(1, "success", data);
    }

    /** 成功且需要一句提示语时用，msg 放在 msg 字段而不是 data，前端可以直接弹出来 */
    public static Result successMsg(String msg) {
        return new Result(1, msg, null);
    }

    public static Result error(String msg) {
        return new Result(0, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
