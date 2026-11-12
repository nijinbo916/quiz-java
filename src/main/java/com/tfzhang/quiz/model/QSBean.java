package com.tfzhang.quiz.model;

/**
 * 前端「添加题目」表单提交上来的数据结构（老师笔记《Quiz后端-6》步骤3）。
 *
 * 前端页面上是题干 + 四个选项 + 一个单选答案，答案用 a/b/c/d 表示，
 * 所以这里 answer 是 String，由 Service 层转成 answerNCorrect。
 */
public class QSBean {

    private String question;

    private String optiona;
    private String optionb;
    private String optionc;
    private String optiond;

    /** "a" / "b" / "c" / "d" */
    private String answer;

    /** 分类，前端下拉框带过来 */
    private String category;

    public QSBean() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptiona() {
        return optiona;
    }

    public void setOptiona(String optiona) {
        this.optiona = optiona;
    }

    public String getOptionb() {
        return optionb;
    }

    public void setOptionb(String optionb) {
        this.optionb = optionb;
    }

    public String getOptionc() {
        return optionc;
    }

    public void setOptionc(String optionc) {
        this.optionc = optionc;
    }

    public String getOptiond() {
        return optiond;
    }

    public void setOptiond(String optiond) {
        this.optiond = optiond;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "QSBean{question='" + question + "', optiona='" + optiona + "', optionb='" + optionb
                + "', optionc='" + optionc + "', optiond='" + optiond + "', answer='" + answer + "'}";
    }
}
