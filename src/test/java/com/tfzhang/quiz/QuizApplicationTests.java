package com.tfzhang.quiz;

import com.tfzhang.quiz.mapper.QuestionMapper;
import com.tfzhang.quiz.mapper.UserMapper;
import com.tfzhang.quiz.model.PageBean;
import com.tfzhang.quiz.model.QSBean;
import com.tfzhang.quiz.model.QSBeanOut;
import com.tfzhang.quiz.model.QSBeanOutManage;
import com.tfzhang.quiz.model.QSBeanPage;
import com.tfzhang.quiz.model.Question;
import com.tfzhang.quiz.model.Result;
import com.tfzhang.quiz.model.User;
import com.tfzhang.quiz.service.QuestionService;
import com.tfzhang.quiz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MyBatis / Service 测试类。
 *
 * 类上加了 @Transactional：每个测试方法跑完自动回滚，测试造的假数据不会留在数据库里。
 */
@SpringBootTest
@Transactional
class QuizApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionService questionService;

    /** 查询全部用户：能把数据库里的用户打印出来就说明 MyBatis 打通了 */
    @Test
    public void testListUser() {
        List<User> userList = userMapper.list();
        System.out.println("查询到 " + userList.size() + " 位用户：");
        userList.forEach(System.out::println);
    }

    /** 新增用户 + 主键回填 + 按 id 查回来（视频 9-4 的 Mapper 部分） */
    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUserName("测试用户_mvn");
        user.setUserPassword("123456");
        user.setIsDelete(0);
        user.setUserRole(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        int rows = userMapper.saveUser(user);
        System.out.println("插入影响行数 = " + rows + "，回填的主键 id = " + user.getId());

        User saved = userMapper.getById(user.getId());
        System.out.println("按 id 查回来：" + saved);
    }

    /** 用户名查重（视频 9-5 的练习）：已存在返回 1，不存在返回 0 */
    @Test
    public void testExistsByName() {
        User user = new User();
        user.setUserName("checkdup");
        user.setUserPassword("123456");
        user.setIsDelete(0);
        user.setUserRole(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.saveUser(user);

        System.out.println("插入后查重 checkdup = " + userMapper.existsByName("checkdup") + "（应为 1）");
        System.out.println("查重 notexists999 = " + userMapper.existsByName("notexists999") + "（应为 0）");
    }

    /** 逻辑删除：按 id 和按用户名各试一次（视频 9-6） */
    @Test
    public void testDeleteUser() {
        User byId = buildUser("del_by_id");
        userMapper.saveUser(byId);
        System.out.println("按 id 删除影响行数 = " + userMapper.deleteUserById(byId.getId()) + "（应为 1）");
        System.out.println("删除后还能查到吗：" + userMapper.getById(byId.getId()) + "（应为 null）");

        User byName = buildUser("del_by_name");
        userMapper.saveUser(byName);
        System.out.println("按用户名删除影响行数 = " + userMapper.deleteUserByUsername("del_by_name") + "（应为 1）");
        System.out.println("删除后还能查到吗：" + userMapper.getById(byName.getId()) + "（应为 null）");
    }

    /** 分页查询（视频 9-7）：总数、第一页、第二页 */
    @Test
    public void testPage() {
        int total = userMapper.count();
        System.out.println("未删除用户总数 = " + total);

        List<User> page1 = userMapper.page(0, 5);
        System.out.println("第 1 页 " + page1.size() + " 条：");
        page1.forEach(u -> System.out.println("   " + u.getId() + " " + u.getUserName()));

        List<User> page2 = userMapper.page(5, 5);
        System.out.println("第 2 页 " + page2.size() + " 条：");
        page2.forEach(u -> System.out.println("   " + u.getId() + " " + u.getUserName()));
    }

    /** 按用户名模糊查询（视频 9-7 第二部分） */
    @Test
    public void testFindByName() {
        List<User> users = userMapper.findByName("小");
        System.out.println("名字里带「小」的用户共 " + users.size() + " 位：");
        users.forEach(u -> System.out.println("   " + u.getId() + " " + u.getUserName()));
    }

    /** Service 层注册：密码加密、用户名查重、各种非法输入 */
    @Test
    public void testServiceAddUser() {
        System.out.println("== 空用户名");
        System.out.println("  " + msg(userService.addUser("", "12345678", "12345678")));

        System.out.println("== 两次密码不一致");
        System.out.println("  " + msg(userService.addUser("tom001", "12345678", "87654321")));

        System.out.println("== 用户名带特殊字符");
        System.out.println("  " + msg(userService.addUser("tom#001", "12345678", "12345678")));

        System.out.println("== 正常注册");
        Result ok = userService.addUser("tom001", "12345678", "12345678");
        System.out.println("  " + msg(ok));

        System.out.println("== 同一个用户名再注册一次（应被查重拦下）");
        System.out.println("  " + msg(userService.addUser("tom001", "12345678", "12345678")));

        System.out.println("== 数据库里存的是密文吗");
        Integer exists = userMapper.existsByName("tom001");
        System.out.println("  tom001 是否已入库：" + exists);
        List<User> found = userMapper.findByName("tom001");
        if (!found.isEmpty()) {
            System.out.println("  库里的密码字段 = " + found.get(0).getUserPassword() + "（应为 32 位 MD5）");
        }
    }

    /** Service 层分页：返回 PageBean（total + 当前页数据，密码已打码） */
    @Test
    public void testServicePage() {
        PageBean bean = userService.page(1, 5);
        System.out.println("总数 = " + bean.getTotal() + "，当前页条数 = " + bean.getRows().size());
        bean.getRows().forEach(u -> System.out.println("   " + u.getId() + " " + u.getUserName() + " / " + u.getUserPassword()));
    }

    /** 物理删除（老师留的作业）：先逻辑删除，再彻底删掉 */
    @Test
    public void testHardDelete() {
        User user = buildUser("hard_del");
        userMapper.saveUser(user);

        System.out.println("未逻辑删除就直接物理删：" + userMapper.hardDeleteById(user.getId()) + "（应为 0，被保护拦住）");
        userMapper.deleteUserById(user.getId());
        System.out.println("逻辑删除后再物理删：" + userMapper.hardDeleteById(user.getId()) + "（应为 1）");
    }

    // ==================== 题目部分（视频 9-8 ~ 10-2） ====================

    /** Mapper 插入题目：四个选项落库 + 自增主键回填 */
    @Test
    public void testInsertQuestion() {
        Question question = buildQuestion("下面哪个方法用于向数组末尾追加元素？", "push");

        int rows = questionMapper.insertQuestion(question);
        System.out.println("插入题目影响行数 = " + rows + "，回填 id = " + question.getId());

        Question saved = questionMapper.getById(question.getId());
        System.out.println("按 id 查回来：" + saved.getQuestionText() + " / " + saved.getCategory());
        assertNotNull(saved.getId());
    }

    /** 逻辑删除：删完再查应该查不到，但数据还在表里 */
    @Test
    public void testDelQuestion() {
        Question question = buildQuestion("逻辑删除测试题，删完就不该出现", "a");
        questionMapper.insertQuestion(question);

        int rows = questionMapper.delQuestionById(question.getId());
        System.out.println("逻辑删除影响行数 = " + rows + "（应为 1）");
        assertEquals(1, rows);
        assertEquals(null, questionMapper.getById(question.getId()));
        assertEquals(0, questionMapper.delQuestionById(question.getId()), "重复删除应返回 0");
    }

    /** 用户端随机取题：固定 5 条，且每条都有 4 个选项和唯一正确答案 */
    @Test
    public void testGetQuestions() {
        List<Question> list = questionMapper.getQuestions();
        System.out.println("随机抽到 " + list.size() + " 道题：");
        list.forEach(q -> System.out.println("   " + q.getId() + " " + q.getQuestionText()));
        assertEquals(5, list.size());
    }

    /** 管理端分页：第一页 5 条，total 是总数 */
    @Test
    public void testQuestionPage() {
        QSBeanPage page = questionService.page(1, 5);
        System.out.println("总题数 = " + page.getTotal() + "，本页 " + page.getQsBeanList().size() + " 条");
        assertTrue(page.getTotal() >= 5);
        assertEquals(5, page.getQsBeanList().size());

        List<QSBeanOutManage> rows = page.getQsBeanList();
        rows.forEach(r -> System.out.println("   " + r.getId() + " [" + r.getAnswerLabel() + "] " + r.getQuestionText()));
    }

    /** 管理端关键词查询：命中题干里带关键词的题，且答案字母正确 */
    @Test
    public void testFindQuestion() {
        QSBeanPage page = questionService.findByName("CSS", 1, 5);
        System.out.println("关键词 CSS 命中 " + page.getTotal() + " 条：");
        page.getQsBeanList().forEach(r -> System.out.println("   " + r.getQuestionText() + " → " + r.getAnswerLabel()));
        assertTrue(page.getTotal() >= 2);

        QSBeanPage empty = questionService.findByName("这个关键词肯定不存在xyz", 1, 5);
        assertEquals(0, empty.getTotal());
    }

    /** 用户端出参格式：{ question, answers:[{text, correct}] } */
    @Test
    public void testConvertToQSBeanList() {
        List<QSBeanOut> list = questionService.getQuestions();
        QSBeanOut first = list.get(0);
        System.out.println("题干：" + first.getQuestion());
        first.getAnswers().forEach(a -> System.out.println("   " + a.getText() + " correct=" + a.isCorrect()));

        assertEquals(4, first.getAnswers().size());
        long correctCount = first.getAnswers().stream().filter(a -> a.isCorrect()).count();
        assertEquals(1, correctCount, "每题应该恰好一个正确答案");
    }

    /** Service 新增：正常提交能插入，答案非法 / 选项缺失要被拦下来 */
    @Test
    public void testServiceAddQuestion() {
        QSBean bean = new QSBean();
        bean.setQuestion("HTTP 中用于提交表单数据的请求方法是？");
        bean.setOptiona("GET");
        bean.setOptionb("POST");
        bean.setOptionc("HEAD");
        bean.setOptiond("OPTIONS");
        bean.setAnswer("b");
        bean.setCategory("网络");

        int rows = questionService.insertQuestion(bean);
        System.out.println("Service 插入影响行数 = " + rows);
        assertEquals(1, rows);

        QSBean badAnswer = copyBean(bean);
        badAnswer.setAnswer("e");
        assertThrows(IllegalArgumentException.class, () -> questionService.insertQuestion(badAnswer));

        QSBean missingOption = copyBean(bean);
        missingOption.setOptionc("  ");
        assertThrows(IllegalArgumentException.class, () -> questionService.insertQuestion(missingOption));
    }

    /** Service 修改：改完题库里的题干和答案都要跟着变 */
    @Test
    public void testServiceUpdateQuestion() {
        QSBean bean = new QSBean();
        bean.setQuestion("修改前的题干，至少四个字");
        bean.setOptiona("选项一");
        bean.setOptionb("选项二");
        bean.setOptionc("选项三");
        bean.setOptiond("选项四");
        bean.setAnswer("a");
        bean.setCategory("HTML");
        questionService.insertQuestion(bean);

        QSBeanPage page = questionService.findByName("修改前的题干", 1, 5);
        Integer id = page.getQsBeanList().get(0).getId();

        QSBean update = copyBean(bean);
        update.setQuestion("修改后的题干，同样四个字以上");
        update.setAnswer("d");
        assertTrue(questionService.updateQuestion(id, update));

        QSBeanPage after = questionService.findByName("修改后的题干", 1, 5);
        assertEquals(1, after.getTotal());
        assertEquals("D", after.getQsBeanList().get(0).getAnswerLabel());
    }

    private Question buildQuestion(String text, String correctOption) {
        Question question = new Question();
        question.setQuestionText(text);
        question.setAnswer1Text("选项A");
        question.setAnswer1Correct("a".equals(correctOption));
        question.setAnswer2Text("选项B");
        question.setAnswer2Correct("b".equals(correctOption));
        question.setAnswer3Text("选项C");
        question.setAnswer3Correct("c".equals(correctOption));
        question.setAnswer4Text("选项D");
        question.setAnswer4Correct("d".equals(correctOption));
        question.setCategory("测试");
        question.setIsDelete(0);
        Date now = new Date();
        question.setCreateTime(now);
        question.setUpdateTime(now);
        return question;
    }

    private QSBean copyBean(QSBean src) {
        QSBean bean = new QSBean();
        bean.setQuestion(src.getQuestion());
        bean.setOptiona(src.getOptiona());
        bean.setOptionb(src.getOptionb());
        bean.setOptionc(src.getOptionc());
        bean.setOptiond(src.getOptiond());
        bean.setAnswer(src.getAnswer());
        bean.setCategory(src.getCategory());
        return bean;
    }

    private User buildUser(String name) {
        User user = new User();
        user.setUserName(name);
        user.setUserPassword("123456");
        user.setIsDelete(0);
        user.setUserRole(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        return user;
    }

    private String msg(Result result) {
        return "code=" + result.getCode() + " msg=" + result.getMsg();
    }
}
