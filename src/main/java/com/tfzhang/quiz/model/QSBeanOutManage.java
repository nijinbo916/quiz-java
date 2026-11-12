package com.tfzhang.quiz.model;

import java.util.List;

/**
 * 管理台题目列表的一行（老师笔记《Quiz后端-7》第 3 节）。
 *
 * 管理页面要看到：题目、四个选项、正确答案字母。
 * 所以 options 是 List&lt;String&gt;，answer 直接给 "A"~"D"，前端不用再算下标。
 */
public class QSBeanOutManage {

    private Integer id;

    private String questionText;

    private List<String> options;

    /** 正确答案的选项文本，方便前端直接打勾 */
    private String answer;

    /** 正确答案字母 A/B/C/D */
    private String answerLabel;

    /** 分类 */
    private String category;

    public QSBeanOutManage() {
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

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerLabel() {
        return answerLabel;
    }

    public void setAnswerLabel(String answerLabel) {
        this.answerLabel = answerLabel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
