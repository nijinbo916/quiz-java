package com.tfzhang.quiz.mapper;

import com.tfzhang.quiz.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 题目表 questions 的 MyBatis 映射接口（老师笔记《Quiz后端-6》《Quiz后端-7》）。
 *
 * 表里四组 answerNText / answerNCorrect 是一一对应的，所以插入、更新时四个选项一起写。
 * 所有查询都带 isDelete = 0，逻辑删除的题目不再出现。
 */
@Mapper
public interface QuestionMapper {

    /** 新增题目，并把自增主键回填到 question.id */
    @Insert("insert into questions(questionText, " +
            "answer1Text, answer1Correct, answer2Text, answer2Correct, " +
            "answer3Text, answer3Correct, answer4Text, answer4Correct, " +
            "category, isDelete, createTime, updateTime) values(" +
            "#{questionText}, " +
            "#{answer1Text}, #{answer1Correct}, #{answer2Text}, #{answer2Correct}, " +
            "#{answer3Text}, #{answer3Correct}, #{answer4Text}, #{answer4Correct}, " +
            "#{category}, #{isDelete}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertQuestion(Question question);

    /** 按 id 逻辑删除：只把 isDelete 置 1，题目还在表里可以追溯 */
    @Update("update questions set isDelete = 1, updateTime = now() where id = #{id} and isDelete = 0")
    int delQuestionById(@Param("id") Integer id);

    /** 按 id 查未删除的题目 */
    @Select("select * from questions where id = #{id} and isDelete = 0")
    Question getById(@Param("id") Integer id);

    /**
     * 用户端随机抽 5 道题。
     * ORDER BY RAND() 会全表排序，题目量大时不适合，但题库只有几十道，用它最简单。
     */
    @Select("select * from questions where isDelete = 0 order by rand() limit 5")
    List<Question> getQuestions();

    /** 未删除的题目总数（管理台分页用） */
    @Select("select count(*) from questions where isDelete = 0")
    int count();

    /** 按题干关键词统计总数（关键词查询分页用） */
    @Select("select count(*) from questions where isDelete = 0 and questionText like concat('%', #{keyword}, '%')")
    int countByKeyword(@Param("keyword") String keyword);

    /** 分页查询：start 是起始行下标，从 0 开始，由 Service 层算好传进来 */
    @Select("select * from questions where isDelete = 0 order by id limit #{start}, #{pageSize}")
    List<Question> page(@Param("start") Integer start, @Param("pageSize") Integer pageSize);

    /** 关键词分页查询 */
    @Select("select * from questions where isDelete = 0 and questionText like concat('%', #{keyword}, '%') " +
            "order by id limit #{start}, #{pageSize}")
    List<Question> pageByKeyword(@Param("keyword") String keyword,
                                 @Param("start") Integer start,
                                 @Param("pageSize") Integer pageSize);

    /** 按题干关键词查询（不分页，笔记里的 findQuestion 接口） */
    @Select("select * from questions where isDelete = 0 and questionText like concat('%', #{keyword}, '%') order by id")
    List<Question> findByKeyword(@Param("keyword") String keyword);

    /** 修改题目内容与四个选项 */
    @Update("update questions set questionText = #{questionText}, " +
            "answer1Text = #{answer1Text}, answer1Correct = #{answer1Correct}, " +
            "answer2Text = #{answer2Text}, answer2Correct = #{answer2Correct}, " +
            "answer3Text = #{answer3Text}, answer3Correct = #{answer3Correct}, " +
            "answer4Text = #{answer4Text}, answer4Correct = #{answer4Correct}, " +
            "category = #{category}, updateTime = now() " +
            "where id = #{id} and isDelete = 0")
    int updateQuestion(Question question);
}
