package com.tfzhang.quiz.service.impl;

import com.tfzhang.quiz.mapper.QuestionMapper;
import com.tfzhang.quiz.model.QSBean;
import com.tfzhang.quiz.model.QSBeanOut;
import com.tfzhang.quiz.model.QSBeanOutManage;
import com.tfzhang.quiz.model.QSBeanPage;
import com.tfzhang.quiz.model.Question;
import com.tfzhang.quiz.service.QuestionService;
import com.tfzhang.quiz.utils.Tools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题目业务实现类（对应视频 9-8 ~ 10-2）。
 *
 * 前端提交的 a/b/c/d 在这里翻译成 answerNCorrect 四个布尔字段，
 * 查询结果再翻译回管理台 / 用户端各自的格式，中间的转换交给 Tools。
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    /** 题干最短长度，避免一不小心提交一个空壳题目 */
    private static final int MIN_TITLE_LENGTH = 4;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public int insertQuestion(QSBean qsBean) {
        validate(qsBean);
        Question question = Tools.convertToQuestion(qsBean);
        return questionMapper.insertQuestion(question);
    }

    @Override
    public boolean delQuestion(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("题目 id 不合法");
        }
        return questionMapper.delQuestionById(id) > 0;
    }

    @Override
    public List<QSBeanOut> getQuestions() {
        List<Question> questionList = questionMapper.getQuestions();
        return Tools.convertToQSBeanList(questionList);
    }

    @Override
    public QSBeanPage page(Integer page, Integer pageSize) {
        return queryByKeyword(null, page, pageSize);
    }

    @Override
    public QSBeanPage findByName(String keyword, Integer page, Integer pageSize) {
        return queryByKeyword(keyword, page, pageSize);
    }

    @Override
    public boolean updateQuestion(Integer id, QSBean qsBean) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("题目 id 不合法");
        }
        if (questionMapper.getById(id) == null) {
            throw new IllegalArgumentException("题目不存在或已被删除");
        }

        validate(qsBean);
        Question question = Tools.convertToQuestion(qsBean);
        question.setId(id);
        return questionMapper.updateQuestion(question) > 0;
    }

    /**
     * 分页查询的公共逻辑：有关键词走关键词统计与查询，没有就走全量。
     * 页码和每页条数在这里兜底，防止前端传 0 或负数导致 limit 报错。
     */
    private QSBeanPage queryByKeyword(String keyword, Integer page, Integer pageSize) {
        int currentPage = (page == null || page < 1) ? 1 : page;
        int size = (pageSize == null || pageSize < 1) ? 5 : pageSize;
        int start = (currentPage - 1) * size;

        String kw = keyword == null ? "" : keyword.trim();

        int total;
        List<Question> questionList;
        if (kw.isEmpty()) {
            total = questionMapper.count();
            questionList = questionMapper.page(start, size);
        } else {
            total = questionMapper.countByKeyword(kw);
            questionList = questionMapper.pageByKeyword(kw, start, size);
        }

        List<QSBeanOutManage> rows = Tools.convertToQSBeanManageList(questionList);
        return new QSBeanPage(total, rows);
    }

    /**
     * 新增和修改共用的校验。
     * 四个选项在数据库里是 NOT NULL 的，所以这里必须先拦下来，否则会抛 SQL 异常给前端看到。
     */
    private void validate(QSBean qsBean) {
        if (qsBean == null) {
            throw new IllegalArgumentException("提交内容不能为空");
        }
        if (StringUtils.isBlank(qsBean.getQuestion())) {
            throw new IllegalArgumentException("题干不能为空");
        }
        if (qsBean.getQuestion().trim().length() < MIN_TITLE_LENGTH) {
            throw new IllegalArgumentException("题干至少 " + MIN_TITLE_LENGTH + " 个字");
        }
        if (StringUtils.isAnyBlank(qsBean.getOptiona(), qsBean.getOptionb(),
                qsBean.getOptionc(), qsBean.getOptiond())) {
            throw new IllegalArgumentException("四个选项都要填");
        }
        if (!Tools.isValidAnswer(qsBean.getAnswer())) {
            throw new IllegalArgumentException("答案只能是 a、b、c、d 之一");
        }
    }
}
