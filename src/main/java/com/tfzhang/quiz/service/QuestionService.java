package com.tfzhang.quiz.service;

import com.tfzhang.quiz.model.QSBean;
import com.tfzhang.quiz.model.QSBeanOut;
import com.tfzhang.quiz.model.QSBeanPage;

import java.util.List;

/**
 * 题目业务接口（老师笔记《Quiz后端-6》《Quiz后端-7》）。
 *
 * 校验全部放在实现类里，Controller 只负责收发参数。
 * 校验不通过时抛 IllegalArgumentException，由 Controller 统一转成 Result.error。
 */
public interface QuestionService {

    /**
     * 新增题目。answer 只接受 a/b/c/d，题干和四个选项都不能为空。
     * @return 影响行数，1 表示插入成功
     */
    int insertQuestion(QSBean qsBean);

    /**
     * 按 id 逻辑删除题目。
     * @return true 表示确实删掉了一条，false 表示题目不存在或已经删过
     */
    boolean delQuestion(Integer id);

    /**
     * 用户端获取题目：随机抽 5 道未删除的题。
     */
    List<QSBeanOut> getQuestions();

    /**
     * 管理台分页查询全部题目。
     * @param page 页码，从 1 开始
     * @param pageSize 每页条数
     */
    QSBeanPage page(Integer page, Integer pageSize);

    /**
     * 管理台按题干关键词分页查询。keyword 为空时等价于查全部。
     */
    QSBeanPage findByName(String keyword, Integer page, Integer pageSize);

    /**
     * 修改题目。前端提交的格式与新增一致。
     */
    boolean updateQuestion(Integer id, QSBean qsBean);
}
