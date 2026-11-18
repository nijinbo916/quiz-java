-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: quiz
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `quiz`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `quiz` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `quiz`;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `questionText` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '题干',
  `answer1Text` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `answer1Correct` tinyint(1) NOT NULL DEFAULT '0',
  `answer2Text` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `answer2Correct` tinyint(1) NOT NULL DEFAULT '0',
  `answer3Text` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `answer3Correct` tinyint(1) NOT NULL DEFAULT '0',
  `answer4Text` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `answer4Correct` tinyint(1) NOT NULL DEFAULT '0',
  `category` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类',
  `isDelete` tinyint DEFAULT '0' COMMENT '0-未删除，1-已逻辑删除',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='题目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (1,'在 HTML 中，用来表示页面标题栏文字的是哪个标签？','<body>',0,'<title>',1,'<head>',0,'<h1>',0,'HTML',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(2,'CSS 中设置文字颜色的属性是？','font-color',0,'text-color',0,'color',1,'foreground',0,'CSS',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(3,'在 JavaScript 中，声明一个不能被重新赋值的变量应使用？','var',0,'let',0,'const',1,'static',0,'JavaScript',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(4,'Vue 中实现双向绑定的指令是？','v-bind',0,'v-on',0,'v-model',1,'v-if',0,'Vue',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(5,'HTTP 状态码 404 表示的含义是？','服务器内部错误',0,'请求成功',0,'资源未找到',1,'没有权限',0,'网络',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(6,'在 HTML 中，创建超链接应使用哪个标签？','<a>',1,'<link>',0,'<href>',0,'<url>',0,'HTML',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(7,'CSS 中用于设置元素外边距的属性是？','margin',1,'padding',0,'border',0,'spacing',0,'CSS',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(8,'在 JavaScript 中，向数组末尾添加元素应使用哪个方法？','shift',0,'push',1,'unshift',0,'pop',0,'JavaScript',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(9,'Vue 中组件挂载完成之后执行的生命周期钩子是？','created',0,'mounted',1,'updated',0,'destroyed',0,'Vue',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(10,'HTTP 中用于获取资源的请求方法是？','POST',0,'PUT',0,'GET',1,'DELETE',0,'网络',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(11,'CSS 中选中 id 为 box 的元素，正确的写法是？','.box',0,'#box',1,'box',0,'*box',0,'CSS',0,'2026-11-12 18:37:26','2026-11-12 18:37:26'),(12,'JavaScript 中判断\"值和类型都相等\"应使用哪个运算符？','==',0,'!=',0,'===',1,'!==',0,'JavaScript',0,'2026-11-12 18:37:26','2026-11-12 18:37:26');
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userName` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
  `userPassword` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint DEFAULT NULL COMMENT '0-未删除，1-已逻辑删除',
  `userRole` int DEFAULT '0' COMMENT '0-普通用户，1-管理员',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','abb283b0200e38c5676e51f4ee23f28b','2026-10-29 20:20:19','2026-10-29 20:20:19',0,1),(2,'吴小诚','abb283b0200e38c5676e51f4ee23f28b','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(3,'王小虎','abb283b0200e38c5676e51f4ee23f28b','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(4,'李小明','8c2970513b9ece35993720af1a65bd48','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(5,'赵小刚','441fd12a3f0c4f4dbe16e2a0c23d1a54','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(6,'孙小丽','a75f8ec7e662ef15fa08714b9aa8ab9d','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(7,'周小强','44c8fda24b6d4b9411f179294341eb6c','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(8,'郑小雨','186627db971d85322830781dc4400479','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(9,'冯小南','e612e85fc3afffc76fba65a7c9876f2b','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(10,'陈小北','bf5254c3fa1bc8a230ca9791756ae57f','2026-10-29 20:20:19','2026-10-29 20:20:19',0,0),(11,'倪金博','21ec8e4aaccd922bbcae4cb720836d5c','2026-10-29 20:20:19','2026-10-29 20:20:19',0,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-18 19:02:32
