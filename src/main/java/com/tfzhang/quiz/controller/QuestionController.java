package com.tfzhang.quiz.controller;

import com.tfzhang.quiz.model.QSBean;
import com.tfzhang.quiz.model.QSBeanOut;
import com.tfzhang.quiz.model.QSBeanPage;
import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 题目相关接口（对应视频 9-8 ~ 10-2）。
 *
 * 用户端要的是「随机 5 道题」→ /getQuestion
 * 管理端要的是「分页列表 + 关键词查询」→ /questions、/findQuestion
 * 两者查的是同一张表，只是出参格式不同，转换在 Tools 里做。
 */
@RestController
@CrossOrigin
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 插入新题目。
     * 前端用 JSON 提交（Content-Type: application/json），所以必须加 @RequestBody，
     * 否则表单格式和 JSON 对不上，后台收到的字段全是 null（笔记里踩过这个坑）。
     */
    @PostMapping("/addQuestion")
    public Result addQuestion(@RequestBody QSBean question) {
        int result = questionService.insertQuestion(question);
        if (result > 0) {
            return Result.successMsg("插入新题目成功");
        }
        return Result.error("插入失败");
    }

    /** 修改题目，id 放在 query 上，内容还是 JSON */
    @PostMapping("/updateQuestion")
    public Result updateQuestion(@RequestParam Integer id, @RequestBody QSBean question) {
        boolean success = questionService.updateQuestion(id, question);
        if (success) {
            return Result.successMsg("修改成功");
        }
        return Result.error("修改失败，题目可能已被删除");
    }

    /** 按 id 逻辑删除题目 */
    @GetMapping("/delQuestion")
    public Result deleteQuestion(Integer id) {
        boolean success = questionService.delQuestion(id);
        if (success) {
            return Result.successMsg("题目删除成功");
        }
        return Result.error("题目不存在或已被删除");
    }

    /** 用户端获取题目：随机 5 道，格式与 quiz-game 里的 quizQuestions 一致 */
    @GetMapping("/getQuestion")
    public Result getQuestion() {
        List<QSBeanOut> list = questionService.getQuestions();
        return Result.success(list);
    }

    /** 管理端分页查询：page 从 1 开始，pageSize 默认 5 */
    @GetMapping("/questions")
    public Result getPage(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "5") Integer pageSize) {
        QSBeanPage qsBeanPage = questionService.page(page, pageSize);
        return Result.success(qsBeanPage);
    }

    /**
     * 管理端按题干关键词查询。
     * 笔记里这个接口直接返回 List；这里做成带 total 的分页结构，
     * 是为了完成笔记留的练习「关键词查询也要分页，超过 5 条只显示 5 条」。
     */
    @GetMapping("/findQuestion")
    public Result findQuestion(@RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "5") Integer pageSize) {
        QSBeanPage qsBeanPage = questionService.findByName(keyword, page, pageSize);
        return Result.success(qsBeanPage);
    }

    /**
     * 业务校验失败统一返回 code=0 + 提示语，而不是 500。
     * 前端拿到 msg 直接弹 message，不需要区分是哪个字段错了。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgument(IllegalArgumentException e) {
        return Result.error(e.getMessage());
    }
}
