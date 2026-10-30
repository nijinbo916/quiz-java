package com.tfzhang.quiz.model;

import java.util.Date;

/**
 * 用户实体类，对应数据库中的 user 表。
 *
 * 说明：老师视频里用 Lombok 的 @Data 自动生成 get/set（视频 9-2 专门讲 IDEA 里 Lombok 报错的问题）。
 * 这里为了不依赖 Lombok 插件也能直接编译运行，改为手写 getter/setter —— 效果完全一样。
 */
public class User {

    /** 用户ID（主键，自增） */
    private Long id;

    /** 用户名 */
    private String userName;

    /** 密码 */
    private String userPassword;

    /** 是否删除：0-未删除，1-已删除（逻辑删除） */
    private Integer isDelete;

    /** 用户角色：0-普通用户，1-管理员 */
    private Integer userRole;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", isDelete=" + isDelete +
                ", userRole=" + userRole +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
