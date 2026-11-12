package com.tfzhang.quiz.model;

import java.util.Date;

/**
 * 题目实体，对应 quiz.questions 表（老师笔记《Quiz后端-6》）。
 *
 * 选项固定 4 个，用 answerNCorrect 标记哪个是正确答案，而不是存一个答案下标——
 * 这样从表里取出来就是完整的题目，前端要什么格式都能拼。
 */
public class Question {

    private Integer id;

    private String questionText;

    private String answer1Text;
    private Boolean answer1Correct;

    private String answer2Text;
    private Boolean answer2Correct;

    private String answer3Text;
    private Boolean answer3Correct;

    private String answer4Text;
    private Boolean answer4Correct;

    /** 分类，管理台用来筛选 */
    private String category;

    /** 是否删除：0 未删除，1 已逻辑删除 */
    private Integer isDelete;

    private Date createTime;

    private Date updateTime;

    public Question() {
        this.isDelete = 0;
        Date now = new Date();
        this.createTime = now;
        this.updateTime = now;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswer1Text() {
        return answer1Text;
    }

    public void setAnswer1Text(String answer1Text) {
        this.answer1Text = answer1Text;
    }

    public Boolean getAnswer1Correct() {
        return answer1Correct;
    }

    public void setAnswer1Correct(Boolean answer1Correct) {
        this.answer1Correct = answer1Correct;
    }

    public String getAnswer2Text() {
        return answer2Text;
    }

    public void setAnswer2Text(String answer2Text) {
        this.answer2Text = answer2Text;
    }

    public Boolean getAnswer2Correct() {
        return answer2Correct;
    }

    public void setAnswer2Correct(Boolean answer2Correct) {
        this.answer2Correct = answer2Correct;
    }

    public String getAnswer3Text() {
        return answer3Text;
    }

    public void setAnswer3Text(String answer3Text) {
        this.answer3Text = answer3Text;
    }

    public Boolean getAnswer3Correct() {
        return answer3Correct;
    }

    public void setAnswer3Correct(Boolean answer3Correct) {
        this.answer3Correct = answer3Correct;
    }

    public String getAnswer4Text() {
        return answer4Text;
    }

    public void setAnswer4Text(String answer4Text) {
        this.answer4Text = answer4Text;
    }

    public Boolean getAnswer4Correct() {
        return answer4Correct;
    }

    public void setAnswer4Correct(Boolean answer4Correct) {
        this.answer4Correct = answer4Correct;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
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
}
