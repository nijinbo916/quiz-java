package com.tfzhang.quiz.model;

import java.util.List;

/**
 * 用户端获取题目时的出参（老师笔记《Quiz后端-7》）。
 *
 * 结构与 quiz-game 里硬编码的 quizQuestions 数组完全一致：
 * { question: "...", answers: [{ text, correct }, ...] }
 */
public class QSBeanOut {

    private String question;

    private List<AnsBean> answers;

    public QSBeanOut() {
    }

    public QSBeanOut(String question, List<AnsBean> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnsBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnsBean> answers) {
        this.answers = answers;
    }
}
