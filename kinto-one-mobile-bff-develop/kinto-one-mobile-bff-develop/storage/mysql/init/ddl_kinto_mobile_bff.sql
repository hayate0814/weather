-- MySQL dump 10.13  Distrib 8.0.33, for Linux (aarch64)
--
-- Host: 127.0.0.1    Database: kinto_vegeta_bff
-- ------------------------------------------------------
-- Server version	8.0.33

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
-- Current Database: `kinto_vegeta_bff`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `kinto_vegeta_bff` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `kinto_vegeta_bff`;

--
-- Table structure for table `login_history`
--

DROP TABLE IF EXISTS `login_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;

CREATE TABLE `login_history` (
  `login_history_id` int NOT NULL AUTO_INCREMENT COMMENT 'ログイン履歴ID',
  `user_id` int DEFAULT NULL COMMENT 'ユーザーID',
  `member_id` varchar(32) NOT NULL COMMENT 'メンバーID',
  `login_datetime` datetime NOT NULL COMMENT '最後ログイン日時',
  PRIMARY KEY (`login_history_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ログイン履歴';

--
-- Table structure for table `member_contract`
--

DROP TABLE IF EXISTS `member_contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_contract` (
  `contract_id` int(11) unsigned NOT NULL COMMENT '契約ID',
  `application_id` int(11) unsigned NOT NULL COMMENT '申込ID',
  `member_id` varchar(32) NOT NULL COMMENT 'メンバーID',
  `old_contract_id` varchar(32) NOT NULL COMMENT '旧契約ID',
  `update_datetime` datetime NOT NULL COMMENT '更新日時',
  `create_datetime` datetime NOT NULL COMMENT '作成日時',
  PRIMARY KEY (`contract_id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会員契約';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member_notification_history`
--

DROP TABLE IF EXISTS `member_notification_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_notification_history` (
  `notification_id` int NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `member_id` varchar(32) NOT NULL COMMENT 'メンバーID',
  `old_contract_id` varchar(32) NOT NULL COMMENT '旧契約ID',
  `notification_kind_sv` tinyint NOT NULL COMMENT '通知種別:1: 契約ステータス変更',
  `title` text NOT NULL COMMENT '通知内容タイトル',
  `content` text NOT NULL COMMENT '通知内容',
  `read_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未読、1:既読',
  `create_datetime` datetime NOT NULL COMMENT '作成日時',
  PRIMARY KEY (`notification_id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会員通知履歴';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification_sent_job`
--

DROP TABLE IF EXISTS `notification_sent_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_sent_job` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '通知送信ID',
  `member_id` varchar(32) NOT NULL COMMENT 'メンバーID',
  `old_contract_id` varchar(32) NOT NULL COMMENT '旧契約ID',
  `notification_kind_sv` tinyint NOT NULL COMMENT '通知種別:1: 契約ステータス変更',
  `title` text NOT NULL COMMENT '通知タイトル',
  `content` text NOT NULL COMMENT '通知内容',
  `sent_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '送信フラグ:0:未送信, 1:送信済',
  `update_datetime` datetime NOT NULL COMMENT '更新日時',
  `create_datetime` datetime NOT NULL COMMENT '作成日時',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Push通知送信Job';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `simulation`
--

DROP TABLE IF EXISTS `simulation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `simulation` (
  `simulation_id` int NOT NULL AUTO_INCREMENT COMMENT 'シュミレーションID',
  `guest_user_id` int DEFAULT NULL COMMENT 'ゲストユーザーID:所有者がゲストユーザーー場合のみ',
  `member_id` varchar(32) DEFAULT NULL COMMENT 'メンバーID:所有者がメンバーの場合のみ',
  `simulation_datetime` datetime NOT NULL COMMENT 'シミュレーション保存日時',
  `title_json` json NOT NULL COMMENT 'タイトル',
  `simulation_json` json NOT NULL COMMENT 'シミュレーション保存データ',
  `update_datetime` datetime NOT NULL COMMENT '更新日時',
  `create_datetime` datetime NOT NULL COMMENT '作成日時',
  PRIMARY KEY (`simulation_id`),
  KEY `idx_guest_user_id` (`guest_user_id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='シミュレーションデータ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `setting_master` 2023-7-7追加
--
DROP TABLE IF EXISTS `setting_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setting_master` (
  `sm_key` varchar(20) NOT NULL COMMENT 'キー',
  `sm_value` varchar(100) DEFAULT NULL COMMENT 'バリュー',
  `update_datetime` datetime NOT NULL COMMENT '更新日時',
  `create_datetime` datetime NOT NULL COMMENT '作成日時',
  PRIMARY KEY (`sm_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='設定マスタ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_device`
--

DROP TABLE IF EXISTS `user_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_device` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT 'ユーザーID',
  `device_code` varchar(200) NOT NULL COMMENT '端末識別コード:uuid発行',
  `device_kind_sv` tinyint NOT NULL COMMENT '端末種別:1: android, 2: ios',
  `notice_token` text COMMENT '通知用トークン',
  `member_id` varchar(32) DEFAULT NULL COMMENT 'メンバーID:ユーザログイン後、会員IDを設定',
  `update_datetime` datetime NOT NULL COMMENT '更新日時',
  `create_datetime` datetime NOT NULL COMMENT '作成日時',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ユーザー端末';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-17 13:58:56
