package com.tfzhang.quiz.model;

/**
 * 用户端答题要用的单个选项：文本 + 是否正确。
 * 对应前端 quiz-game 里 { text, correct } 的结构。
 */
public class AnsBean {

    private String text;
    private boolean correct;

    public AnsBean(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
