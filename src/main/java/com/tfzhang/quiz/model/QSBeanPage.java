package com.tfzhang.quiz.model;

import java.util.List;

/**
 * 管理台题目分页结果的封装（老师笔记《Quiz后端-7》）。
 *
 * 与 PageBean 的区别只是行数据类型不同：这里装的是 QSBeanOutManage。
 */
public class QSBeanPage {

    private Integer total;

    private List<QSBeanOutManage> qsBeanList;

    public QSBeanPage() {
    }

    public QSBeanPage(Integer total, List<QSBeanOutManage> qsBeanList) {
        this.total = total;
        this.qsBeanList = qsBeanList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<QSBeanOutManage> getQsBeanList() {
        return qsBeanList;
    }

    public void setQsBeanList(List<QSBeanOutManage> qsBeanList) {
        this.qsBeanList = qsBeanList;
    }
}
