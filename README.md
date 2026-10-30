# Quiz 后端（Spring Boot + MyBatis + MySQL）

对应 Web 开发技术课程的两节课：
- **第六次课 · Task 10-23**：装 MySQL + IDEA、用 MyBatis 访问 MySQL、跑通 Controller（视频 9-1 ~ 9-3）
- **第七次课 · Task 10-30**：用户的增删改查 + 三层架构改造（视频 9-4 ~ 9-7）

参考笔记：`gitee.com/bigbug55/webdesign` 的《quiz-java后端》系列（记录 1 ~ 5）。

> 两次课的课件原文都是「今日自己学习上述基础内容，**无作业需要提交**」。所以这个仓库不是提交项，而是把课上要求真正跑通的产物。

---

## 一、环境（本机已就绪）

| 组件 | 版本 / 位置 | 说明 |
| --- | --- | --- |
| JDK | 27（`C:\Program Files\Java\jdk-27`） | 项目按 Java 17 字节码编译，JDK 27 可直接运行 |
| Maven | 3.9.16（`D:\Program\apache-maven-3.9.16`） | 已在 `C:\Users\hp\.m2\settings.xml` 配好**阿里云镜像**（老师笔记的推荐做法） |
| IDEA | IntelliJ IDEA CE（`D:\Program\IntelliJIDEA-CE`） | 社区版够用；数据库面板在右侧 Database 栏 |
| MySQL | 8.0.40 **便携版**（`D:\Program\mysql8`） | 无需管理员安装，`root / 123456`，端口 3306 |

### MySQL 怎么启停

| 操作 | 方式 |
| --- | --- |
| 启动 | 双击 `D:\Program\mysql8\启动MySQL.bat` |
| 停止 | 双击 `D:\Program\mysql8\停止MySQL.bat` |
| 命令行启动 | `D:\Program\mysql8\mysql-8.0.40-winx64\bin\mysqld.exe --defaults-file=D:\Program\mysql8\my.ini --console` |
| 命令行登录 | `D:\Program\mysql8\mysql-8.0.40-winx64\bin\mysql.exe -u root -p123456 -h 127.0.0.1` |

配置文件 `D:\Program\mysql8\my.ini`：端口 3306、字符集 utf8mb4、数据目录 `D:\Program\mysql8\data`。

想让 MySQL 开机自启（需要**管理员** PowerShell）：

```powershell
sc.exe create MySQL80 binPath= "D:\Program\mysql8\mysql-8.0.40-winx64\bin\mysqld.exe --defaults-file=D:\Program\mysql8\my.ini" start= auto
sc.exe start MySQL80
```

---

## 二、数据库设计

执行 `sql/init.sql` 会创建两个库：

| 库 | 表 | 用途 |
| --- | --- | --- |
| `mytest` | `user(id,name,age,email)` | 老师笔记里 MyBatis 的第一个练手表，5 条示例数据 |
| `quiz` | `user(id,userName,userPassword,updateTime,createTime,isDelete,userRole)` | 项目正式用户表，11 条数据 |
| `quiz` | `question(...)` | 题目表，为下节课「前后端联调」预留，5 条数据 |

`userRole`：0 普通用户 / 1 管理员；`isDelete`：0 未删除 / 1 已逻辑删除（删除只改标记，不真删数据）。

---

## 三、项目结构

```
quiz-java/
├── pom.xml                          # web + mybatis + mysql 驱动 + commons-lang3
├── sql/init.sql                     # 建库建表 + 演示数据
└── src/
    ├── main/java/com/tfzhang/quiz/
    │   ├── QuizApplication.java     # 启动类，@MapperScan 扫描 mapper 包
    │   ├── model/
    │   │   ├── User.java            # 用户实体（对应 user 表）
    │   │   ├── PageBean.java        # 分页结果（total + rows）
    │   │   ├── SimpleUser.java      # 实体参数演示用
    │   │   └── Result.java          # 统一响应结果（code/msg/data）
    │   ├── mapper/UserMapper.java   # MyBatis 接口，SQL 写在注解上
    │   ├── service/
    │   │   ├── UserService.java     # 业务接口（接口与实现分离）
    │   │   └── impl/UserServiceImpl.java  # 业务实现：校验、查重、加密、分页
    │   └── controller/
    │       ├── HelloController.java # 9-3 的四个需求
    │       └── UserController.java  # 只收参数、返回 Result，逻辑全在 Service
    ├── main/resources/application.yml
    └── test/java/com/tfzhang/quiz/QuizApplicationTests.java
```

### 三层架构怎么分的

| 层 | 类 | 职责 |
| --- | --- | --- |
| Controller | `UserController` | 接收请求参数、调用 Service、把结果包成 `Result` 返回。**不写业务判断** |
| Service | `UserService` / `UserServiceImpl` | 判空、两次密码一致性、用户名特殊字符、用户名查重、MD5 加盐、分页起止行计算、密码打码 |
| Mapper | `UserMapper` | 只负责 SQL，注解写在方法上 |

这样改的好处很直接：`Controller` 从原来 70 多行全是 `if` 的代码，变成每行一句调用；以后要换实现（比如加缓存）只动 Service。

---

## 四、三步验证（都已实测通过）

### ① MyBatis 访问 MySQL（对应笔记「步骤5：编写测试类」）

```bash
mvn test
```

结果：`Tests run: 3, Failures: 0, Errors: 0`，控制台输出：

```
新增成功，回填的主键 id = 13
按 id 查回来：User{id=13, userName='测试用户_mvn', ..., isDelete=0, userRole=0}
逻辑删除后是否还能查到：null
```

### ② Controller 接收请求（对应笔记需求 1-4）

```bash
mvn spring-boot:run     # 或 java -jar target/quiz-0.0.1-SNAPSHOT.jar
```

| 请求 | 实测返回 |
| --- | --- |
| `GET /hello` | `Hello World` |
| `GET /simpleParam?name=tom&age=10` | `ok`（终端打印 `name=tom, age=10`） |
| `GET /simpleUser?name=tom&age=10` | `ok`（Spring 自动封装成 SimpleUser 对象） |
| `GET /result/simpleUser?name=tom&age=10` | `{"code":1,"msg":"success","data":{"name":"tom","age":10}}` |
| `POST /result/simpleUser`（表单） | 同上，验证 POST 也能收参数 |

### ③ 全链路：浏览器 → Controller → Service → Mapper → MySQL

见下面第五节「10-30 用户增删改查接口」的实测表，两节课的接口现在走的是同一套三层架构。

---

## 五、10-30 用户增删改查接口（对应视频 9-4 ~ 9-7，全部实测通过）

启动服务后直接访问（`--server.port=8080` 视环境加不加）：

| 接口 | 说明 | 实测返回 |
| --- | --- | --- |
| `GET /register?username=&password=&checkpassword=` | 注册新用户（9-4 / 9-5） | 填 `tom001 / 12345678 / 12345678` → `{"code":1,"msg":"success","data":{"id":26,...}}` |
| 同上，用户名重复 | 用户名查重 | `{"code":0,"msg":"用户名已存在"}` |
| 同上，两次密码不同 | 密码一致性 | `{"code":0,"msg":"两次输入的密码不一致"}` |
| 同上，用户名带 `#` | 特殊字符校验 | `{"code":0,"msg":"用户名包含特殊字符"}` |
| 同上，三项都空 | 判空 | `{"code":0,"msg":"用户名或密码为空"}` |
| `GET /users?page=1&pageSize=5` | 分页查询（9-7） | `total=12`，首页返回 id 1~5，密码全部打码成 `******` |
| `GET /users?page=3&pageSize=5` | 最后一页 | `total=12`，返回 2 条（第 11、12 条） |
| `GET /users/all` | 全部未删除用户 | 11 条示例数据 |
| `GET /users/2` | 按 id 查单个 | 返回「吴小诚」 |
| `GET /findUser?keyword=小` | 按用户名模糊查（9-7） | 命中 9 人（名字里带「小」） |
| `GET /findUser?keyword=tom` | 同上 | 命中 1 人（tom001） |
| `GET /deleteById?id=26` | 按 id 逻辑删除（9-6） | `{"code":1,"msg":"用户已删除"}`，总数从 12 → 11 |
| 重复调一次 | 幂等提示 | `{"code":0,"msg":"用户不存在或已被删除"}` |
| `GET /deleteByName?username=tom001` | 按用户名逻辑删除（9-6） | 删除成功 |
| `DELETE /hardDelete/{id}` | **物理删除（老师留的作业）** | 未逻辑删除就删 → 被拦下返回 `code=0`；先逻辑删除再删 → `code=1`，库中记录消失 |

### 数据库侧确认

```sql
-- 逻辑删除只是把标记位改成 1，数据仍在表里
select id, userName, isDelete, updateTime from quiz.user where id = 26;
-- 26  tom001  1  2026-10-30 10:08:08

-- 物理删除之后，这行才真正消失
select count(*) from quiz.user where userName = 'tom003';   -- 0
```

### 密码怎么存

注册时按老师笔记的方式做 **MD5 加盐**：`md5("com.quiz" + 明文密码)`，库里存的是 32 位十六进制串（例如 `tom001 / 12345678` → `52a54d7cf94cf8cb2b28dcf7fcfdd4c6`）。查询接口一律把密码替换成 `******` 再返回，避免密文外泄。

### 测试

```bash
mvn test
```

结果：`Tests run: 9, Failures: 0, Errors: 0`。9 个用例覆盖：查全部、插入 + 主键回填、用户名查重、按 id / 用户名逻辑删除、分页、模糊查询、Service 注册的四种非法输入 + 正常注册、物理删除保护。

测试类上加了 `@Transactional`，跑完自动回滚，不会在数据库里留下假数据。

### 给下节课（前后端联调）的准备

`UserController` 上加了 `@CrossOrigin`，实测预检请求返回：

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET
```

Vue 开发服务器（8081）直接请求 8080 不会被浏览器拦，不用再折腾代理。

---

## 六、在 IDEA 里打开

1. IDEA → Open → 选 `quiz-java` 文件夹（不是 `src`），等 Maven 索引完成
2. 右侧 **Database** 面板 → `+` → Data Source → MySQL，填 `localhost:3306`、`root`、`123456`，测试连接后即可可视化看表
3. 打开 `QuizApplication.java`，点 `main` 左边的绿色三角运行；或打开 `QuizApplicationTests` 右键跑单个测试

---

## 七、这一步踩过的坑（记录下来，后面还会遇到）

1. **Windows 上装 MySQL 不必用 MSI**：官方 zip 解压 + `mysqld --initialize-insecure` + 自己的 `my.ini` 就能跑，不需要管理员、不写注册表，卸载只是删目录。
2. **`skip-name-resolve` 会导致 `root@localhost` 连不上 127.0.0.1**（报 `ERROR 1130 Host '127.0.0.1' is not allowed`），所以 `my.ini` 里去掉了这一项。
3. **中文写库报 `Data too long for column`**：命令行导入 sql 时没带字符集，连接按 gbk 解析导致字节翻倍。解决：`mysql --default-character-set=utf8mb4 ... < init.sql`。
4. **端口被环境变量顶掉**：某些终端环境里存在 `SERVER__PORT`，Spring 会覆盖 `application.yml` 的 8080，报 `Port xxxxx was already in use`。启动时显式加 `--server.port=8080` 即可。
5. **Lombok（视频 9-2 的坑）**：本项目没引 Lombok，getter/setter 手写。如果要跟着视频用 `@Data`，需要在 IDEA 装 Lombok 插件并开启 `Enable annotation processing`；JDK 27 下老版本 Lombok 会报 `java.lang.NoSuchFieldError: com.sun.tools.javac.tree.JCTree$JCImport.qualid`，升级到 1.18.34+ 才能编译。
6. **依赖下载慢**：`~/.m2/settings.xml` 配阿里云镜像后，首次构建 32 秒完成；不配的话 Spring Boot 全家桶要下很久。
7. **`mvn test` 报 `CannotCreateTransaction: Could not open JDBC Connection`**：不是代码问题，是 MySQL 没启动。先在任务管理器看有没有 `mysqld.exe`，没有就双击 `启动MySQL.bat`。
8. **在命令行用 `&` 后台起 mysqld 会被回收**：命令一结束进程就没了（表现是启动日志正常、几秒后 3306 不再监听）。要让数据库常驻，双击 `启动MySQL.bat`，它用 `start` 独立拉起进程。
