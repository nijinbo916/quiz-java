package com.tfzhang.quiz.utils;

import com.tfzhang.quiz.model.AnsBean;
import com.tfzhang.quiz.model.QSBean;
import com.tfzhang.quiz.model.QSBeanOut;
import com.tfzhang.quiz.model.QSBeanOutManage;
import com.tfzhang.quiz.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 题目实体在「数据库形态」与「前端要的形态」之间的转换工具（老师笔记《Quiz后端-7》）。
 *
 * 数据库里一行题目是四个 answerNText + 四个 answerNCorrect；
 * 而用户端要 { question, answers:[{text,correct}] }，管理台要 { id, questionText, options[], answer }。
 * 转换集中放在这里，Service 层就不用到处写重复代码。
 */
public class Tools {

    private static final String[] LABELS = {"A", "B", "C", "D"};

    private Tools() {
    }

    /**
     * 数据库题目 → 用户端答题格式。
     */
    public static List<QSBeanOut> convertToQSBeanList(List<Question> questionList) {
        List<QSBeanOut> result = new ArrayList<>();
        if (questionList == null) {
            return result;
        }

        for (Question question : questionList) {
            QSBeanOut bean = new QSBeanOut();
            bean.setQuestion(question.getQuestionText());

            List<AnsBean> answers = new ArrayList<>(4);
            answers.add(new AnsBean(nullToEmpty(question.getAnswer1Text()), isTrue(question.getAnswer1Correct())));
            answers.add(new AnsBean(nullToEmpty(question.getAnswer2Text()), isTrue(question.getAnswer2Correct())));
            answers.add(new AnsBean(nullToEmpty(question.getAnswer3Text()), isTrue(question.getAnswer3Correct())));
            answers.add(new AnsBean(nullToEmpty(question.getAnswer4Text()), isTrue(question.getAnswer4Correct())));
            bean.setAnswers(answers);

            result.add(bean);
        }
        return result;
    }

    /**
     * 数据库题目 → 管理台列表格式。
     * 已逻辑删除的题目直接跳过，避免它们出现在表格里。
     */
    public static List<QSBeanOutManage> convertToQSBeanManageList(List<Question> questionList) {
        List<QSBeanOutManage> result = new ArrayList<>();
        if (questionList == null) {
            return result;
        }

        for (Question question : questionList) {
            if (question.getIsDelete() != null && question.getIsDelete() == 1) {
                continue;
            }

            QSBeanOutManage bean = new QSBeanOutManage();
            bean.setId(question.getId());
            bean.setQuestionText(question.getQuestionText());
            bean.setCategory(question.getCategory());

            List<String> options = new ArrayList<>(4);
            options.add(nullToEmpty(question.getAnswer1Text()));
            options.add(nullToEmpty(question.getAnswer2Text()));
            options.add(nullToEmpty(question.getAnswer3Text()));
            options.add(nullToEmpty(question.getAnswer4Text()));
            bean.setOptions(options);

            bean.setAnswer(determineCorrectAnswer(question));
            bean.setAnswerLabel(determineAnswerLabel(question));
            result.add(bean);
        }
        return result;
    }

    /**
     * 找出正确选项的文本，没有则返回空串。
     */
    public static String determineCorrectAnswer(Question question) {
        if (isTrue(question.getAnswer1Correct())) {
            return nullToEmpty(question.getAnswer1Text());
        }
        if (isTrue(question.getAnswer2Correct())) {
            return nullToEmpty(question.getAnswer2Text());
        }
        if (isTrue(question.getAnswer3Correct())) {
            return nullToEmpty(question.getAnswer3Text());
        }
        if (isTrue(question.getAnswer4Correct())) {
            return nullToEmpty(question.getAnswer4Text());
        }
        return "";
    }

    /**
     * 找出正确选项的字母 A/B/C/D，没有则返回空串。
     */
    public static String determineAnswerLabel(Question question) {
        if (isTrue(question.getAnswer1Correct())) {
            return "A";
        }
        if (isTrue(question.getAnswer2Correct())) {
            return "B";
        }
        if (isTrue(question.getAnswer3Correct())) {
            return "C";
        }
        if (isTrue(question.getAnswer4Correct())) {
            return "D";
        }
        return "";
    }

    /**
     * 前端表单（答案用 a/b/c/d 表示）→ 数据库实体。
     */
    public static Question convertToQuestion(QSBean qsBean) {
        String ans = qsBean.getAnswer() == null ? "" : qsBean.getAnswer().trim().toLowerCase();

        Question q = new Question();
        q.setQuestionText(trim(qsBean.getQuestion()));
        q.setCategory(trim(qsBean.getCategory()));

        q.setAnswer1Text(trim(qsBean.getOptiona()));
        q.setAnswer1Correct("a".equals(ans));

        q.setAnswer2Text(trim(qsBean.getOptionb()));
        q.setAnswer2Correct("b".equals(ans));

        q.setAnswer3Text(trim(qsBean.getOptionc()));
        q.setAnswer3Correct("c".equals(ans));

        q.setAnswer4Text(trim(qsBean.getOptiond()));
        q.setAnswer4Correct("d".equals(ans));

        q.setIsDelete(0);
        Date now = new Date();
        q.setCreateTime(now);
        q.setUpdateTime(now);
        return q;
    }

    /**
     * a/b/c/d 是否合法。
     */
    public static boolean isValidAnswer(String answer) {
        if (answer == null) {
            return false;
        }
        return Arrays.asList("a", "b", "c", "d").contains(answer.trim().toLowerCase());
    }

    /**
     * 答案字母 → 选项下标 0~3，非法返回 -1。
     */
    public static int answerIndex(String answer) {
        if (!isValidAnswer(answer)) {
            return -1;
        }
        return Arrays.asList("a", "b", "c", "d").indexOf(answer.trim().toLowerCase());
    }

    public static String labelOf(int index) {
        if (index < 0 || index >= LABELS.length) {
            return "";
        }
        return LABELS[index];
    }

    private static boolean isTrue(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
