-- ============================================================
-- Quiz 后端数据库初始化脚本（第六次课 10-23）
-- 用法：mysql -u root -p < sql/init.sql
-- ============================================================

-- 1) 课堂练习用的示例库 mytest（老师笔记里 MyBatis 第一个测试用的就是它）
CREATE DATABASE IF NOT EXISTS mytest DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

DROP TABLE IF EXISTS mytest.user;
CREATE TABLE mytest.user
(
    id    BIGINT NOT NULL COMMENT '主键ID',
    name  VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    age   INT NULL DEFAULT NULL COMMENT '年龄',
    email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (id)
);

INSERT INTO mytest.user (id, name, age, email)
VALUES (1, 'Jone', 18, 'test1@baomidou.com'),
       (2, 'Jack', 20, 'test2@baomidou.com'),
       (3, 'Tom', 28, 'test3@baomidou.com'),
       (4, 'Sandy', 21, 'test4@baomidou.com'),
       (5, 'Billie', 24, 'test5@baomidou.com');

-- 2) 项目正式库 quiz（后台管理的用户表）
CREATE DATABASE IF NOT EXISTS quiz DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

DROP TABLE IF EXISTS quiz.user;
CREATE TABLE quiz.user
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    userName     VARCHAR(256)                       NULL COMMENT '用户名',
    userPassword VARCHAR(512)                       NULL COMMENT '密码',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    isDelete     TINYINT                            NULL COMMENT '0-未删除，1-已逻辑删除',
    userRole     INT      DEFAULT 0                 NULL COMMENT '0-普通用户，1-管理员'
) COMMENT '用户表';

-- 密码统一为 md5("com.quiz" + 明文)，与注册逻辑一致（123456 → abb283...）
INSERT INTO quiz.user (userName, userPassword, isDelete, userRole)
VALUES ('admin', 'abb283b0200e38c5676e51f4ee23f28b', 0, 1),
       ('吴小诚', 'abb283b0200e38c5676e51f4ee23f28b', 0, 0),
       ('王小虎', 'abb283b0200e38c5676e51f4ee23f28b', 0, 0),
       ('李小明', '8c2970513b9ece35993720af1a65bd48', 0, 0),
       ('赵小刚', '441fd12a3f0c4f4dbe16e2a0c23d1a54', 0, 0),
       ('孙小丽', 'a75f8ec7e662ef15fa08714b9aa8ab9d', 0, 0),
       ('周小强', '44c8fda24b6d4b9411f179294341eb6c', 0, 0),
       ('郑小雨', '186627db971d85322830781dc4400479', 0, 0),
       ('冯小南', 'e612e85fc3afffc76fba65a7c9876f2b', 0, 0),
       ('陈小北', 'bf5254c3fa1bc8a230ca9791756ae57f', 0, 0),
       ('倪金博', '21ec8e4aaccd922bbcae4cb720836d5c', 0, 1);

-- 3) 题目表（第八次课 11-6：题目页与前后端联调）
-- 字段与老师笔记《Quiz后端-6》一致：选项固定 4 个，用 answer1~4Correct 标记正确答案；
-- category 是额外加的，方便管理台按分类筛选
DROP TABLE IF EXISTS quiz.questions;
CREATE TABLE quiz.questions
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    questionText   VARCHAR(255)                       NOT NULL COMMENT '题干',
    answer1Text    VARCHAR(255)                       NOT NULL,
    answer1Correct BOOLEAN      DEFAULT FALSE         NOT NULL,
    answer2Text    VARCHAR(255)                       NOT NULL,
    answer2Correct BOOLEAN      DEFAULT FALSE         NOT NULL,
    answer3Text    VARCHAR(255)                       NOT NULL,
    answer3Correct BOOLEAN      DEFAULT FALSE         NOT NULL,
    answer4Text    VARCHAR(255)                       NOT NULL,
    answer4Correct BOOLEAN      DEFAULT FALSE         NOT NULL,
    category       VARCHAR(64)                        NULL COMMENT '分类',
    isDelete       TINYINT      DEFAULT 0             NULL COMMENT '0-未删除，1-已逻辑删除',
    createTime     DATETIME     DEFAULT CURRENT_TIMESTAMP NULL,
    updateTime     DATETIME     DEFAULT CURRENT_TIMESTAMP NULL
) COMMENT '题目表';

INSERT INTO quiz.questions (questionText, answer1Text, answer1Correct, answer2Text, answer2Correct,
                            answer3Text, answer3Correct, answer4Text, answer4Correct, category, isDelete)
VALUES ('在 HTML 中，用来表示页面标题栏文字的是哪个标签？', '<body>', FALSE, '<title>', TRUE,
        '<head>', FALSE, '<h1>', FALSE, 'HTML', 0),
       ('CSS 中设置文字颜色的属性是？', 'font-color', FALSE, 'text-color', FALSE,
        'color', TRUE, 'foreground', FALSE, 'CSS', 0),
       ('在 JavaScript 中，声明一个不能被重新赋值的变量应使用？', 'var', FALSE, 'let', FALSE,
        'const', TRUE, 'static', FALSE, 'JavaScript', 0),
       ('Vue 中实现双向绑定的指令是？', 'v-bind', FALSE, 'v-on', FALSE,
        'v-model', TRUE, 'v-if', FALSE, 'Vue', 0),
       ('HTTP 状态码 404 表示的含义是？', '服务器内部错误', FALSE, '请求成功', FALSE,
        '资源未找到', TRUE, '没有权限', FALSE, '网络', 0),
       ('在 HTML 中，创建超链接应使用哪个标签？', '<a>', TRUE, '<link>', FALSE,
        '<href>', FALSE, '<url>', FALSE, 'HTML', 0),
       ('CSS 中用于设置元素外边距的属性是？', 'margin', TRUE, 'padding', FALSE,
        'border', FALSE, 'spacing', FALSE, 'CSS', 0),
       ('在 JavaScript 中，向数组末尾添加元素应使用哪个方法？', 'shift', FALSE, 'push', TRUE,
        'unshift', FALSE, 'pop', FALSE, 'JavaScript', 0),
       ('Vue 中组件挂载完成之后执行的生命周期钩子是？', 'created', FALSE, 'mounted', TRUE,
        'updated', FALSE, 'destroyed', FALSE, 'Vue', 0),
       ('HTTP 中用于获取资源的请求方法是？', 'POST', FALSE, 'PUT', FALSE,
        'GET', TRUE, 'DELETE', FALSE, '网络', 0),
       ('CSS 中选中 id 为 box 的元素，正确的写法是？', '.box', FALSE, '#box', TRUE,
        'box', FALSE, '*box', FALSE, 'CSS', 0),
       ('JavaScript 中判断"值和类型都相等"应使用哪个运算符？', '==', FALSE, '!=', FALSE,
        '===', TRUE, '!==', FALSE, 'JavaScript', 0);
