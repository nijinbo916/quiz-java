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

INSERT INTO quiz.user (userName, userPassword, isDelete, userRole)
VALUES ('admin', '123456', 0, 1),
       ('吴小诚', '123456', 0, 0),
       ('王小虎', '123456', 0, 0),
       ('李小明', '888888', 0, 0),
       ('赵小刚', '666666', 0, 0),
       ('孙小丽', '000000', 0, 0),
       ('周小强', '111111', 0, 0),
       ('郑小雨', '123123', 0, 0),
       ('冯小南', 'ab123456', 0, 0),
       ('陈小北', 'qwerty', 0, 0),
       ('倪金博', 'admin888', 0, 1);

-- 3) 题目表（下节课做前后端联调时要用的，先建好）
DROP TABLE IF EXISTS quiz.question;
CREATE TABLE quiz.question
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(512)                       NOT NULL COMMENT '题干',
    category   VARCHAR(64)                        NULL COMMENT '分类',
    optionA    VARCHAR(256)                       NULL,
    optionB    VARCHAR(256)                       NULL,
    optionC    VARCHAR(256)                       NULL,
    optionD    VARCHAR(256)                       NULL,
    answer     INT      DEFAULT 0                 NULL COMMENT '正确答案下标 0-3',
    isDelete   TINYINT  DEFAULT 0                 NULL,
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NULL
) COMMENT '题目表';

INSERT INTO quiz.question (title, category, optionA, optionB, optionC, optionD, answer)
VALUES ('在 HTML 中，用来表示页面标题栏文字的是哪个标签？', 'HTML', '<body>', '<title>', '<head>', '<h1>', 1),
       ('CSS 中设置文字颜色的属性是？', 'CSS', 'font-color', 'text-color', 'color', 'foreground', 2),
       ('在 JavaScript 中，声明一个不能被重新赋值的变量应使用？', 'JavaScript', 'var', 'let', 'const', 'static', 2),
       ('Vue 中实现双向绑定的指令是？', 'Vue', 'v-bind', 'v-on', 'v-model', 'v-if', 2),
       ('HTTP 状态码 404 表示的含义是？', '网络', '服务器内部错误', '请求成功', '资源未找到', '没有权限', 2);
