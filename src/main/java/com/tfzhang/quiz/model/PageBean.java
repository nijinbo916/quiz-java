package com.tfzhang.quiz.model;

import java.util.List;

/**
 * 分页查询的结果封装（老师笔记「查询用户」步骤1）。
 *
 * total：满足条件的总记录数，前端用它算总页数；
 * rows ：当前这一页的数据。
 */
public class PageBean {

    private Integer total;
    private List<User> rows;

    public PageBean() {
    }

    public PageBean(Integer total, List<User> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<User> getRows() {
        return rows;
    }

    public void setRows(List<User> rows) {
        this.rows = rows;
    }
}
