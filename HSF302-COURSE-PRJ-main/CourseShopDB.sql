-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: course_shop
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9emlp6m95v5er2bcqkjsw48he` (`user_id`),
  CONSTRAINT `FKg5uhi8vpsuy0lgloxk2h4w5o6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (4,4),(2,9),(1,11),(3,25),(5,26);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `price` decimal(38,2) DEFAULT NULL,
  `cart_id` bigint DEFAULT NULL,
  `course_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1uobyhgl1wvgt1jpccia8xxs3` (`cart_id`),
  KEY `FKbunrbg0wdvvukbm9lhyfdfeei` (`course_id`),
  CONSTRAINT `FK1uobyhgl1wvgt1jpccia8xxs3` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  CONSTRAINT `FKbunrbg0wdvvukbm9lhyfdfeei` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES (18,200000.00,1,8),(33,NULL,3,17),(34,299000.00,3,16);
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `certificate`
--

DROP TABLE IF EXISTS `certificate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificate` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `issued_date` datetime(6) NOT NULL,
  `course_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnnm153gu9kaknjb58euxms2uk` (`course_id`),
  KEY `FKtnnj9ktwn18vtvap4yuptwxhg` (`user_id`),
  CONSTRAINT `FKnnm153gu9kaknjb58euxms2uk` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `FKtnnj9ktwn18vtvap4yuptwxhg` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `certificate`
--

LOCK TABLES `certificate` WRITE;
/*!40000 ALTER TABLE `certificate` DISABLE KEYS */;
INSERT INTO `certificate` VALUES (2,'2025-07-24 12:37:05.877240',9,5),(3,'2025-07-24 13:46:34.591045',8,9),(4,'2025-08-20 15:20:59.899208',3,11),(5,'2025-10-23 16:25:05.195976',11,25),(6,'2026-01-23 17:15:34.939396',17,9);
/*!40000 ALTER TABLE `certificate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `content` varchar(10000) DEFAULT NULL,
  `cover_image` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `enrolled_count` bigint DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `topic_id` bigint DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKafystt9u49ap9lu9u8f2f089w` (`creator_id`),
  KEY `FKokaxyfpv8p583w8yspapfb2ar` (`topic_id`),
  CONSTRAINT `FKafystt9u49ap9lu9u8f2f089w` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKokaxyfpv8p583w8yspapfb2ar` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2025-06-27 00:00:00.000000','Khóa học: Lập trình Java cơ bản.',3,3,NULL,'APPROVED','Lập trình Java cơ bản','2026-01-07 10:11:51.808515',4,3,199000.00),(2,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2024-09-08 00:00:00.000000','Khóa học: JavaScript cho người mới bắt đầu.',3,1,NULL,'APPROVED','JavaScript cho người mới bắt đầu','2025-08-21 21:05:38.509321',4,3,250000.00),(3,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2025-03-04 00:00:00.000000','Khóa học: Khóa học Python nâng cao.',4,2,NULL,'APPROVED','Khóa học Python nâng cao','2025-12-20 02:55:12.613691',4,7,399000.00),(4,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2025-06-04 00:00:00.000000','Khóa học: C từ A đến Z.',2,1,NULL,'APPROVED','C từ A đến Z','2025-08-21 13:58:41.024265',4,1,200000.00),(5,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2024-08-17 00:00:00.000000','Khóa học: Học C++ qua dự án.',2,1,NULL,'APPROVED','Học C++ qua dự án','2025-12-20 02:14:51.254169',4,7,249000.00),(6,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2024-10-23 00:00:00.000000','Khóa học: Kotlin cho lập trình Android.',5,0,NULL,'APPROVED','Kotlin cho lập trình Android',NULL,4,1,499000.00),(7,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2024-10-11 00:00:00.000000','Khóa học: Swift cơ bản cho iOS.',2,3,NULL,'APPROVED','Swift cơ bản cho iOS','2025-12-20 03:06:01.316415',4,2,200000.00),(8,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2025-06-26 00:00:00.000000','Khóa học: Xây dựng web với JavaScript.',1,2,NULL,'APPROVED','Xây dựng web với JavaScript','2025-12-20 02:42:01.743074',4,1,200000.00),(9,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2>\r\n<hr>\r\n<p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p>\r\n<p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p>\r\n<h2>Những hành động cần làm để học lập trình hiệu quả</h2>\r\n<hr>\r\n<ul style=\"list-style-type: none; padding-left: 0;\">\r\n<li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li>\r\n<li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li>\r\n<li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li>\r\n<li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li>\r\n<li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li>\r\n</ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2025-02-21 00:00:00.000000','Khóa học: Java nâng cao: Spring Boot.',5,2,NULL,'APPROVED','Java nâng cao: Spring Boot','2026-01-07 10:17:10.777182',4,4,949000.00),(10,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2024-11-13 00:00:00.000000','Khóa học: Lập trình nhúng với C.',1,0,NULL,'APPROVED','Lập trình nhúng với C',NULL,4,6,200000.00),(11,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2025-06-30 00:00:00.000000','Khóa học: OOP trong Python.',5,5,NULL,'APPROVED','OOP trong Python','2026-01-07 10:15:47.574719',4,6,200000.00),(12,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2025-03-06 00:00:00.000000','Khóa học: Dự án thực tế với Kotlin.',2,1,NULL,'APPROVED','Dự án thực tế với Kotlin','2025-12-27 19:03:55.322579',4,5,200000.00),(13,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2024-12-16 00:00:00.000000','Khóa học: Lập trình game với C++.',4,1,NULL,'APPROVED','Lập trình game với C++','2025-08-21 21:05:38.506322',4,1,199000.00),(14,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2024-12-27 00:00:00.000000','Khóa học: Tự học Swift hiệu quả.',4,2,NULL,'APPROVED','Tự học Swift hiệu quả','2025-12-20 03:01:22.957566',4,2,199000.00),(15,_binary '','<h2>Lợi ích của việc học lập trình đối với tư duy và tương lai</h2><hr><p>Học lập trình giúp phát triển tư duy logic, khả năng giải quyết vấn đề và sự kiên nhẫn. Nó cũng góp phần rèn luyện kỹ năng làm việc nhóm, tư duy phản biện và khả năng tự học. Ngoài ra, kỹ năng lập trình mở ra nhiều cơ hội nghề nghiệp trong thời đại công nghệ số, từ phát triển phần mềm, ứng dụng di động đến trí tuệ nhân tạo và an ninh mạng.</p><p>Việc học lập trình không chỉ dành cho người làm trong ngành CNTT mà còn là kỹ năng quan trọng trong nhiều lĩnh vực hiện đại. Khuyến khích học lập trình từ sớm sẽ giúp học sinh, sinh viên chủ động hơn trong việc tiếp cận công nghệ và xây dựng tương lai bền vững.</p><h2>Những hành động cần làm để học lập trình hiệu quả</h2><hr><ul style=\"list-style-type: none; padding-left: 0;\"><li style=\"margin-bottom: 8px;\">✓ Tích cực tham gia các khóa học, câu lạc bộ hoặc cuộc thi lập trình</li><li style=\"margin-bottom: 8px;\">✓ Thực hành viết code thường xuyên để rèn kỹ năng</li><li style=\"margin-bottom: 8px;\">✓ Tìm hiểu kiến thức lập trình qua tài liệu, video và trang web uy tín</li><li style=\"margin-bottom: 8px;\">✓ Nhận sự hướng dẫn từ giảng viên, mentor hoặc cộng đồng lập trình</li><li style=\"margin-bottom: 8px;\">✓ Xây dựng dự án thực tế để áp dụng kiến thức và tạo danh mục cá nhân</li></ul>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753335230/upload/file_p4uuko.jpg','2025-06-08 00:00:00.000000','Khóa học: Giới thiệu về Python và AI.',3,3,NULL,'APPROVED','Giới thiệu về Python và AI','2026-01-07 10:17:46.679963',4,4,199000.00),(16,_binary '','<p><strong>Khóa học Lập trình</strong></p>\r\n<ol>\r\n<li><em>Nắm vững kiến thức</em></li>\r\n</ol>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1753340064/upload/file_cd9kui.jpg','2025-07-24 13:53:51.288727','Khóa học lập trình python',6,4,NULL,'APPROVED','Khóa Học Lập trình AI','2026-01-25 19:28:48.164986',4,3,299000.00),(17,_binary '','<p> </p>\r\n<h2 style=\"text-align: center;\">Welcome to the TinyMCE editor demo!</h2>\r\n<p>Please try out the features provided in this basic example.<br>Note that any <strong>MoxieManager</strong> file and image management functionality in this example is part of our commercial offering – the demo is to show the integration.</p>\r\n<h2>Got questions or need help?</h2>\r\n<ul>\r\n<li>Our <a href=\"../../6/\">documentation</a> is a great resource for learning how to configure TinyMCE.</li>\r\n<li>Have a specific question? Try the <a href=\"https://stackoverflow.com/questions/tagged/tinymce\" target=\"_blank\" rel=\"noopener\"><code>tinymce</code> tag at Stack Overflow</a>.</li>\r\n<li>We also offer enterprise grade support as part of <a href=\"../../../../pricing\">TinyMCE premium plans</a>.</li>\r\n</ul>\r\n<h2>A simple table to play with</h2>\r\n<table style=\"border-collapse: collapse; width: 100%;\" border=\"1\">\r\n<thead>\r\n<tr>\r\n<th>Product</th>\r\n<th>Cost</th>\r\n<th>Really?</th>\r\n</tr>\r\n</thead>\r\n<tbody>\r\n<tr>\r\n<td>TinyMCE</td>\r\n<td>Free</td>\r\n<td>YES!</td>\r\n</tr>\r\n<tr>\r\n<td>Plupload</td>\r\n<td>Free</td>\r\n<td>YES!</td>\r\n</tr>\r\n</tbody>\r\n</table>\r\n<h2>Found a bug?</h2>\r\n<p>If you think you have found a bug please create an issue on the <a href=\"https://github.com/tinymce/tinymce/issues\">GitHub repo</a> to report it to the developers.</p>\r\n<h2>Finally ...</h2>\r\n<p>Don\'t forget to check out our other product <a href=\"http://www.plupload.com\" target=\"_blank\" rel=\"noopener\">Plupload</a>, your ultimate upload solution featuring HTML5 upload support.</p>\r\n<p>Thanks for supporting TinyMCE! We hope it helps you and your users create great content.<br>All the best from the TinyMCE team.</p>','https://res.cloudinary.com/dhtjbtn1o/image/upload/v1761212468/upload/file_jf8sna.jpg','2025-10-23 16:41:09.106374','Khóa học siêu vip',10,3,NULL,'APPROVED','React Pro','2026-01-07 10:27:15.367932',1,1,2500000.00);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_enrollment`
--

DROP TABLE IF EXISTS `course_enrollment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_enrollment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `completion_date` datetime(6) DEFAULT NULL,
  `enrollment_date` datetime(6) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `status` enum('COMPLETED','IN_PROGRESS','NOT_ENROLLED') NOT NULL,
  `course_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmdu3eh7r8fvaemtwyps4dtqoh` (`course_id`),
  KEY `FK2sssnm92tadmir6xegxb5duw3` (`user_id`),
  CONSTRAINT `FK2sssnm92tadmir6xegxb5duw3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmdu3eh7r8fvaemtwyps4dtqoh` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_enrollment`
--

LOCK TABLES `course_enrollment` WRITE;
/*!40000 ALTER TABLE `course_enrollment` DISABLE KEYS */;
INSERT INTO `course_enrollment` VALUES (2,'2025-07-24 12:37:05.866643','2025-07-24 12:30:32.371045',100,'COMPLETED',9,5),(3,'2025-07-24 13:46:34.580587','2025-07-24 13:45:20.658066',100,'COMPLETED',8,9),(4,'2025-08-20 15:20:59.893204','2025-08-20 15:06:52.751868',100,'COMPLETED',3,11),(5,NULL,'2025-08-20 15:06:52.768871',0,'IN_PROGRESS',11,11),(6,NULL,'2025-08-20 15:08:24.845776',0,'IN_PROGRESS',16,11),(8,NULL,'2025-08-20 16:25:57.135084',0,'IN_PROGRESS',7,11),(9,NULL,'2025-08-21 13:42:00.329394',16.666666666666668,'IN_PROGRESS',11,9),(10,NULL,'2025-08-21 13:42:00.340021',20,'IN_PROGRESS',1,9),(11,NULL,'2025-08-21 13:58:41.008994',0,'IN_PROGRESS',4,9),(12,NULL,'2025-08-21 13:58:41.017262',0,'IN_PROGRESS',15,9),(13,NULL,'2025-08-21 13:59:55.414717',0,'IN_PROGRESS',7,9),(15,NULL,'2025-08-21 21:05:38.473487',0,'IN_PROGRESS',13,9),(16,NULL,'2025-08-21 21:05:38.495001',0,'IN_PROGRESS',2,9),(17,'2025-10-23 16:25:05.187820','2025-10-23 16:24:01.564369',100,'COMPLETED',11,25),(18,NULL,'2025-12-20 02:14:51.204300',0,'IN_PROGRESS',5,9),(19,NULL,'2025-12-20 02:14:51.234333',0,'IN_PROGRESS',14,9),(20,'2026-01-23 17:15:34.936144','2025-12-20 02:21:19.524674',100,'COMPLETED',17,9),(21,NULL,'2025-12-20 02:23:54.090668',0,'IN_PROGRESS',17,4),(22,NULL,'2025-12-20 02:27:53.490306',0,'IN_PROGRESS',16,4),(23,NULL,'2025-12-20 02:30:20.813385',0,'IN_PROGRESS',15,4),(24,NULL,'2025-12-20 02:42:01.735919',0,'IN_PROGRESS',8,4),(25,NULL,'2025-12-20 02:46:04.465705',0,'IN_PROGRESS',11,4),(26,NULL,'2025-12-20 02:50:12.083946',0,'IN_PROGRESS',1,4),(27,NULL,'2025-12-20 02:55:12.602598',0,'IN_PROGRESS',3,4),(28,NULL,'2025-12-20 03:01:22.949068',0,'IN_PROGRESS',14,4),(29,NULL,'2025-12-20 03:06:01.308130',0,'IN_PROGRESS',7,4),(30,NULL,'2025-12-27 19:03:55.299269',0,'IN_PROGRESS',12,4),(31,NULL,'2026-01-07 10:11:51.792009',0,'IN_PROGRESS',1,26),(32,NULL,'2026-01-07 10:12:24.923104',0,'IN_PROGRESS',16,26),(33,NULL,'2026-01-07 10:15:47.563280',0,'IN_PROGRESS',11,26),(34,NULL,'2026-01-07 10:17:10.762659',0,'IN_PROGRESS',9,26),(35,NULL,'2026-01-07 10:17:46.668901',0,'IN_PROGRESS',15,26),(36,NULL,'2026-01-07 10:27:15.355371',0,'IN_PROGRESS',17,26),(37,NULL,'2026-01-25 19:28:48.146722',0,'IN_PROGRESS',16,9);
/*!40000 ALTER TABLE `course_enrollment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_module`
--

DROP TABLE IF EXISTS `course_module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_module` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `order_index` int DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `course_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkge7sg0xxyo0sxgfelpavhjdj` (`course_id`),
  CONSTRAINT `FKkge7sg0xxyo0sxgfelpavhjdj` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_module`
--

LOCK TABLES `course_module` WRITE;
/*!40000 ALTER TABLE `course_module` DISABLE KEYS */;
INSERT INTO `course_module` VALUES (1,'2025-06-27 00:00:00.000000',1,'Module 1 của khóa học 1',NULL,1),(2,'2025-06-27 00:00:00.000000',2,'Module 2 của khóa học 1',NULL,1),(3,'2024-09-08 00:00:00.000000',1,'Module 1 của khóa học 2',NULL,2),(4,'2024-09-08 00:00:00.000000',2,'Module 2 của khóa học 2',NULL,2),(5,'2024-09-08 00:00:00.000000',3,'Module 3 của khóa học 2',NULL,2),(6,'2024-09-08 00:00:00.000000',4,'Module 4 của khóa học 2',NULL,2),(7,'2025-03-04 00:00:00.000000',1,'Module 1 của khóa học 3',NULL,3),(8,'2025-03-04 00:00:00.000000',2,'Module 2 của khóa học 3',NULL,3),(9,'2025-03-04 00:00:00.000000',3,'Module 3 của khóa học 3',NULL,3),(10,'2025-06-04 00:00:00.000000',1,'Module 1 của khóa học 4',NULL,4),(11,'2025-06-04 00:00:00.000000',2,'Module 2 của khóa học 4',NULL,4),(12,'2025-06-04 00:00:00.000000',3,'Module 3 của khóa học 4',NULL,4),(13,'2024-08-17 00:00:00.000000',1,'Module 1 của khóa học 5',NULL,5),(14,'2024-08-17 00:00:00.000000',2,'Module 2 của khóa học 5',NULL,5),(15,'2024-10-23 00:00:00.000000',1,'Module 1 của khóa học 6',NULL,6),(16,'2024-10-23 00:00:00.000000',2,'Module 2 của khóa học 6',NULL,6),(17,'2024-10-23 00:00:00.000000',3,'Module 3 của khóa học 6',NULL,6),(18,'2024-10-11 00:00:00.000000',1,'Module 1 của khóa học 7',NULL,7),(19,'2024-10-11 00:00:00.000000',2,'Module 2 của khóa học 7',NULL,7),(20,'2025-06-26 00:00:00.000000',1,'Module 1 của khóa học 8',NULL,8),(21,'2025-06-26 00:00:00.000000',2,'Module 2 của khóa học 8',NULL,8),(22,'2025-06-26 00:00:00.000000',3,'Module 3 của khóa học 8',NULL,8),(25,'2024-11-13 00:00:00.000000',1,'Module 1 của khóa học 10',NULL,10),(26,'2024-11-13 00:00:00.000000',2,'Module 2 của khóa học 10',NULL,10),(27,'2024-11-13 00:00:00.000000',3,'Module 3 của khóa học 10',NULL,10),(28,'2025-06-30 00:00:00.000000',1,'Module 1 của khóa học 11',NULL,11),(29,'2025-06-30 00:00:00.000000',2,'Module 2 của khóa học 11',NULL,11),(30,'2025-03-06 00:00:00.000000',1,'Module 1 của khóa học 12',NULL,12),(31,'2025-03-06 00:00:00.000000',2,'Module 2 của khóa học 12',NULL,12),(32,'2024-12-16 00:00:00.000000',1,'Module 1 của khóa học 13',NULL,13),(33,'2024-12-16 00:00:00.000000',2,'Module 2 của khóa học 13',NULL,13),(34,'2024-12-27 00:00:00.000000',1,'Module 1 của khóa học 14',NULL,14),(35,'2024-12-27 00:00:00.000000',2,'Module 2 của khóa học 14',NULL,14),(36,'2024-12-27 00:00:00.000000',3,'Module 3 của khóa học 14',NULL,14),(37,'2025-06-08 00:00:00.000000',1,'Module 1 của khóa học 15',NULL,15),(38,'2025-06-08 00:00:00.000000',2,'Module 2 của khóa học 15',NULL,15),(39,'2025-07-24 12:33:51.098563',1,'Module 1 của khóa học 9',NULL,9),(40,'2025-07-24 12:33:51.105196',2,'Module 2 của khóa học 9',NULL,9),(42,'2025-07-24 13:54:25.456203',1,'Kiến thức cơ bản',NULL,16),(43,'2025-10-23 16:41:09.124361',1,'Phần 1',NULL,17);
/*!40000 ALTER TABLE `course_module` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_video`
--

DROP TABLE IF EXISTS `course_video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_video` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `video_url` varchar(255) DEFAULT NULL,
  `course_module_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9oessblguwdj7p21kmmnicpwo` (`course_module_id`),
  CONSTRAINT `FK9oessblguwdj7p21kmmnicpwo` FOREIGN KEY (`course_module_id`) REFERENCES `course_module` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_video`
--

LOCK TABLES `course_video` WRITE;
/*!40000 ALTER TABLE `course_video` DISABLE KEYS */;
INSERT INTO `course_video` VALUES (1,'Video 1 của module 1','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',1),(2,'Video 2 của module 1','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',1),(3,'Video 1 của module 2','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',2),(4,'Video 2 của module 2','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',2),(5,'Video 3 của module 2','https://youtu.be/vwNtqw0Xv3g?si=9_YDrhe4mS0nNNWf',2),(6,'Video 1 của module 3','https://youtu.be/3tZ_KSJwnRA?si=uO02TnDxe5ApUlHd',3),(7,'Video 2 của module 3','https://youtu.be/1uIeosY2mOU?si=nYh08jiECL9FH8ha',3),(8,'Video 3 của module 3','https://youtu.be/d26FPmk50sQ?si=K1WwOo1IwWjP9z0-',3),(9,'Video 4 của module 3','https://youtu.be/4p80FKBGJWM?si=Nks-pd32NSahRlMJ',3),(10,'Video 1 của module 4','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',4),(11,'Video 2 của module 4','https://youtu.be/v0vgN9QqF08?si=koM75Vo1NeK7-wJ0',4),(12,'Video 1 của module 5','https://youtu.be/3tZ_KSJwnRA?si=uO02TnDxe5ApUlHd',5),(13,'Video 2 của module 5','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',5),(14,'Video 3 của module 5','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',5),(15,'Video 4 của module 5','https://youtu.be/1uIeosY2mOU?si=nYh08jiECL9FH8ha',5),(16,'Video 1 của module 6','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',6),(17,'Video 1 của module 7','https://youtu.be/PRaTYZrurgI?si=vuT2y4QAhYuguuOI',7),(18,'Video 2 của module 7','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',7),(19,'Video 3 của module 7','https://youtu.be/vwNtqw0Xv3g?si=9_YDrhe4mS0nNNWf',7),(20,'Video 1 của module 8','https://youtu.be/m-uI-sk27kE?si=CBRcF6RUrTzhZYV0',8),(21,'Video 2 của module 8','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',8),(22,'Video 3 của module 8','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',8),(23,'Video 1 của module 9','https://youtu.be/d26FPmk50sQ?si=K1WwOo1IwWjP9z0-',9),(24,'Video 2 của module 9','https://youtu.be/4p80FKBGJWM?si=Nks-pd32NSahRlMJ',9),(25,'Video 1 của module 10','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',10),(26,'Video 2 của module 10','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',10),(27,'Video 1 của module 11','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',11),(28,'Video 2 của module 11','https://youtu.be/3tZ_KSJwnRA?si=uO02TnDxe5ApUlHd',11),(29,'Video 3 của module 11','https://youtu.be/1uIeosY2mOU?si=nYh08jiECL9FH8ha',11),(30,'Video 1 của module 12','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',12),(31,'Video 2 của module 12','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',12),(32,'Video 3 của module 12','https://youtu.be/PRaTYZrurgI?si=vuT2y4QAhYuguuOI',12),(33,'Video 1 của module 13','https://youtu.be/vwNtqw0Xv3g?si=9_YDrhe4mS0nNNWf',13),(34,'Video 2 của module 13','https://youtu.be/4p80FKBGJWM?si=Nks-pd32NSahRlMJ',13),(35,'Video 3 của module 13','https://youtu.be/3tZ_KSJwnRA?si=uO02TnDxe5ApUlHd',13),(36,'Video 1 của module 14','https://youtu.be/d26FPmk50sQ?si=K1WwOo1IwWjP9z0-',14),(37,'Video 2 của module 14','https://youtu.be/1uIeosY2mOU?si=nYh08jiECL9FH8ha',14),(38,'Video 3 của module 14','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',14),(39,'Video 1 của module 15','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',15),(40,'Video 2 của module 15','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',15),(41,'Video 1 của module 16','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',16),(42,'Video 2 của module 16','https://youtu.be/PRaTYZrurgI?si=vuT2y4QAhYuguuOI',16),(43,'Video 3 của module 16','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',16),(44,'Video 1 của module 17','https://youtu.be/v0vgN9QqF08?si=koM75Vo1NeK7-wJ0',17),(45,'Video 2 của module 17','https://youtu.be/d26FPmk50sQ?si=K1WwOo1IwWjP9z0-',17),(46,'Video 1 của module 18','https://youtu.be/m-uI-sk27kE?si=CBRcF6RUrTzhZYV0',18),(47,'Video 2 của module 18','https://youtu.be/3tZ_KSJwnRA?si=uO02TnDxe5ApUlHd',18),(48,'Video 3 của module 18','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',18),(49,'Video 1 của module 19','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',19),(50,'Video 2 của module 19','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',19),(51,'Video 3 của module 19','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',19),(52,'Video 1 của module 20','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',20),(53,'Video 2 của module 20','https://youtu.be/4p80FKBGJWM?si=Nks-pd32NSahRlMJ',20),(54,'Video 1 của module 21','https://youtu.be/1uIeosY2mOU?si=nYh08jiECL9FH8ha',21),(55,'Video 2 của module 21','https://youtu.be/PRaTYZrurgI?si=vuT2y4QAhYuguuOI',21),(56,'Video 3 của module 21','https://youtu.be/vwNtqw0Xv3g?si=9_YDrhe4mS0nNNWf',21),(57,'Video 4 của module 21','https://youtu.be/d26FPmk50sQ?si=K1WwOo1IwWjP9z0-',21),(58,'Video 1 của module 22','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',22),(59,'Video 2 của module 22','https://youtu.be/m-uI-sk27kE?si=CBRcF6RUrTzhZYV0',22),(66,'Video 1 của module 25','https://youtu.be/v0vgN9QqF08?si=koM75Vo1NeK7-wJ0',25),(67,'Video 2 của module 25','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',25),(68,'Video 1 của module 26','https://youtu.be/3tZ_KSJwnRA?si=uO02TnDxe5ApUlHd',26),(69,'Video 2 của module 26','https://youtu.be/1uIeosY2mOU?si=nYh08jiECL9FH8ha',26),(70,'Video 1 của module 27','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',27),(71,'Video 2 của module 27','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',27),(72,'Video 3 của module 27','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',27),(73,'Video 1 của module 28','https://youtu.be/d26FPmk50sQ?si=K1WwOo1IwWjP9z0-',28),(74,'Video 2 của module 28','https://youtu.be/vwNtqw0Xv3g?si=9_YDrhe4mS0nNNWf',28),(75,'Video 3 của module 28','https://youtu.be/4p80FKBGJWM?si=Nks-pd32NSahRlMJ',28),(76,'Video 1 của module 29','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',29),(77,'Video 2 của module 29','https://youtu.be/PRaTYZrurgI?si=vuT2y4QAhYuguuOI',29),(78,'Video 3 của module 29','https://youtu.be/m-uI-sk27kE?si=CBRcF6RUrTzhZYV0',29),(79,'Video 1 của module 30','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',30),(80,'Video 2 của module 30','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',30),(81,'Video 3 của module 30','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',30),(82,'Video 1 của module 31','https://youtu.be/1uIeosY2mOU?si=nYh08jiECL9FH8ha',31),(83,'Video 2 của module 31','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',31),(84,'Video 1 của module 32','https://youtu.be/4p80FKBGJWM?si=Nks-pd32NSahRlMJ',32),(85,'Video 2 của module 32','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',32),(86,'Video 3 của module 32','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',32),(87,'Video 1 của module 33','https://youtu.be/d26FPmk50sQ?si=K1WwOo1IwWjP9z0-',33),(88,'Video 2 của module 33','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',33),(89,'Video 1 của module 34','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',34),(90,'Video 2 của module 34','https://youtu.be/PRaTYZrurgI?si=vuT2y4QAhYuguuOI',34),(91,'Video 1 của module 35','https://youtu.be/3tZ_KSJwnRA?si=uO02TnDxe5ApUlHd',35),(92,'Video 2 của module 35','https://youtu.be/v0vgN9QqF08?si=koM75Vo1NeK7-wJ0',35),(93,'Video 3 của module 35','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',35),(94,'Video 1 của module 36','https://youtu.be/vwNtqw0Xv3g?si=9_YDrhe4mS0nNNWf',36),(95,'Video 2 của module 36','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',36),(96,'Video 3 của module 36','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',36),(97,'Video 1 của module 37','https://youtu.be/4p80FKBGJWM?si=Nks-pd32NSahRlMJ',37),(98,'Video 2 của module 37','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',37),(99,'Video 3 của module 37','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',37),(100,'Video 4 của module 37','https://youtu.be/PRaTYZrurgI?si=vuT2y4QAhYuguuOI',37),(101,'Video 1 của module 38','https://youtu.be/m-uI-sk27kE?si=CBRcF6RUrTzhZYV0',38),(102,'Video 2 của module 38','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',38),(103,'Video 3 của module 38','https://youtu.be/vwNtqw0Xv3g?si=9_YDrhe4mS0nNNWf',38),(104,'Video 1 của module 23','https://youtu.be/05ITPWai1lE?si=xjTC9e2XM4PuaagJ',39),(105,'Video 2 của module 23','https://youtu.be/WGtyzv5Ml6Q?si=VtwZZuQbdS1mlnPr',39),(106,'Video 3 của module 23','https://youtu.be/4p80FKBGJWM?si=Nks-pd32NSahRlMJ',39),(107,'Video 1 của module 24','https://youtu.be/fvSvlrm2rEg?si=JsgV3a8FpT0x-vKJ',40),(108,'Video 2 của module 24','https://youtu.be/0nC7H4t5Fzg?si=kjwRn-uKdZs8QgiT',40),(109,'Video 3 của module 24','https://youtu.be/kczMqcfhGAY?si=WZpHEigUGNfkpuuE',40),(111,'kiến thức','https://www.youtube.com/watch?v=def456uvw',42),(112,'React 1','https://www.youtube.com/watch?v=2Nk_6fnpiHI&t=1284s',43);
/*!40000 ALTER TABLE `course_video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invalidated_token`
--

DROP TABLE IF EXISTS `invalidated_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invalidated_token` (
  `id` varchar(255) NOT NULL,
  `expiration_time` datetime(6) DEFAULT NULL,
  `token` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invalidated_token`
--

LOCK TABLES `invalidated_token` WRITE;
/*!40000 ALTER TABLE `invalidated_token` DISABLE KEYS */;
INSERT INTO `invalidated_token` VALUES ('0fe95c62-f05a-4902-b600-88006263bb52','2025-07-19 20:53:58.000000','eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc1MjkzMzIzOCwiaWF0IjoxNzUyOTMxNDM4LCJ1c2VySWQiOjEsImF1dGhvcml0aWVzIjpbIkFETUlOIl0sImVtYWlsIjoiYWRtaW4yMEBnbWFpbC5jb20iLCJqdGkiOiIwZmU5NWM2Mi1mMDVhLTQ5MDItYjYwMC04ODAwNjI2M2JiNTIifQ.xmXed4LKmzHPqHwkWwF8Jv5EREGXMKHudWF5B1Sn6CKLaLH3lO53hsvN3TcS2Abq'),('48741e21-6315-497d-8cc1-dbb05b49de85','2025-07-19 21:01:46.000000','eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc1MjkzMzcwNiwiaWF0IjoxNzUyOTMxOTA2LCJ1c2VySWQiOjEsImF1dGhvcml0aWVzIjpbIkFETUlOIl0sImVtYWlsIjoiYWRtaW4yMEBnbWFpbC5jb20iLCJqdGkiOiI0ODc0MWUyMS02MzE1LTQ5N2QtOGNjMS1kYmIwNWI0OWRlODUifQ.UYQybTDqxBulXRqpswE-QJjqxpNz2RiwOsmLiX6DC2OcO0Eun6hLf6mO7muPB4dh'),('4e28ff7c-6c55-4f92-ad5e-dd42bc7d464c','2025-07-24 12:56:48.000000','eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc1MzMzNjYwOCwiaWF0IjoxNzUzMzM0ODA4LCJ1c2VySWQiOjEsImF1dGhvcml0aWVzIjpbIkFETUlOIl0sImVtYWlsIjoiYWRtaW4yMEBnbWFpbC5jb20iLCJqdGkiOiI0ZTI4ZmY3Yy02YzU1LTRmOTItYWQ1ZS1kZDQyYmM3ZDQ2NGMifQ.UIWljqweAIygD0102YGCrrcAoN0b-XpH2SGt8b5o3_7nuuMXwmn4nWFRm22Cl7Z_'),('8c1bb337-fb0d-4634-805b-5a3855a27758','2025-07-19 21:03:45.000000','eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc1MjkzMzgyNSwiaWF0IjoxNzUyOTMyMDI1LCJ1c2VySWQiOjEsImF1dGhvcml0aWVzIjpbIkFETUlOIl0sImVtYWlsIjoiYWRtaW4yMEBnbWFpbC5jb20iLCJqdGkiOiI4YzFiYjMzNy1mYjBkLTQ2MzQtODA1Yi01YTM4NTVhMjc3NTgifQ.cEFjRJXcjaz7tFkVYirW35KnTYkkd3DvOiApFE7vVTSvvKmGUFXi14DJbYbdDSgY'),('a8004c0b-74b2-4d1f-b605-31be964a0dbb','2025-07-24 14:17:13.000000','eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc1MzM0MTQzMywiaWF0IjoxNzUzMzM5NjMzLCJ1c2VySWQiOjEsImF1dGhvcml0aWVzIjpbIkFETUlOIl0sImVtYWlsIjoiYWRtaW4yMEBnbWFpbC5jb20iLCJqdGkiOiJhODAwNGMwYi03NGIyLTRkMWYtYjYwNS0zMWJlOTY0YTBkYmIifQ.BxZ6ULaoYlWemnRBBS_FcoJBxgfkMJ-9vqx5g71RWmtClribAwXQJnEAd3khi4QM'),('efcfb647-0afc-487d-8c94-f8fd7fea7037','2025-10-23 16:57:56.000000','eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc2MTIxMzQ3NiwiaWF0IjoxNzYxMjExNjc2LCJ1c2VySWQiOjEsImF1dGhvcml0aWVzIjpbIkFETUlOIl0sImVtYWlsIjoiYWRtaW4yMEBnbWFpbC5jb20iLCJqdGkiOiJlZmNmYjY0Ny0wYWZjLTQ4N2QtOGM5NC1mOGZkN2ZlYTcwMzcifQ.hlpUVmoP9j0O18Y-ZWgK11vUObcaj4OBUW8accW9kcQRHGzhaZMHeo9_TsIrzpfm');
/*!40000 ALTER TABLE `invalidated_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `price` decimal(38,2) DEFAULT NULL,
  `course_id` bigint NOT NULL,
  `order_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt6mbaqi6t2vtxfl1dp98jdjxk` (`course_id`),
  KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
  CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKt6mbaqi6t2vtxfl1dp98jdjxk` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,399000.00,3,'d710a58d-1fe2-4644-8fc7-e1be5792a081'),(2,200000.00,11,'d710a58d-1fe2-4644-8fc7-e1be5792a081'),(3,299000.00,16,'b84fee18-9ec8-493c-bda2-c3a1eea83bbe'),(4,200000.00,4,'13e991ef-09b0-4d5c-bdd9-7ccdbf5efb6e'),(5,199000.00,15,'13e991ef-09b0-4d5c-bdd9-7ccdbf5efb6e'),(6,200000.00,8,'e75dfe15-2a29-4cfc-a803-666175ea1e1a'),(7,200000.00,8,'70541ebd-0134-4546-bd7c-55d5a9ff3cf8'),(8,200000.00,7,'41b98dce-20e0-4c22-a9e3-dceb29589e30'),(9,399000.00,3,'063d1b72-d96b-41d1-bea1-7c478e4e9216'),(10,200000.00,11,'063d1b72-d96b-41d1-bea1-7c478e4e9216'),(11,200000.00,11,'1a4e08a7-7b24-4503-9f5b-45a18e15f221'),(12,199000.00,1,'1a4e08a7-7b24-4503-9f5b-45a18e15f221'),(13,200000.00,11,'f35b9005-3c94-496d-b307-c3ad5fe5610c'),(14,199000.00,1,'f35b9005-3c94-496d-b307-c3ad5fe5610c'),(15,200000.00,4,'8917d9d4-b4c7-489a-8d33-267a9586d83c'),(16,199000.00,15,'8917d9d4-b4c7-489a-8d33-267a9586d83c'),(17,200000.00,7,'84ea4aa4-b566-48f4-a52b-b73dd2cdf7b4'),(18,399000.00,3,'83d5b81a-b0f7-4f04-a3fc-9c5eba8823fa'),(19,399000.00,3,'d30a7855-e9ab-4966-ab6e-a97f651793de'),(20,199000.00,13,'5abe3b82-0d96-479f-bc7f-02bb1cf8d2f5'),(21,199000.00,13,'5fbb5af2-159a-4f8f-9df5-dc536822a942'),(22,250000.00,2,'5fbb5af2-159a-4f8f-9df5-dc536822a942'),(23,249000.00,5,'d704240c-1213-4ce4-abfe-e1509ef37ff9'),(24,249000.00,5,'3ebb5825-d69a-43a5-bf9f-4f721df0ea27'),(25,200000.00,11,'e3a4e4c6-f75b-4704-b1ef-fa533a78f02f'),(26,0.00,17,'bcc56cd9-db0a-48f8-bf04-05fd7d09d136'),(27,0.00,17,'8e217824-6320-47b9-b059-9d229252c815'),(28,199000.00,14,'59f74f1f-fb53-4911-b425-0d6aacec3056'),(29,199000.00,14,'ec9e83da-b4bc-4af2-b084-8f9c31110cbd'),(30,249000.00,5,'aa714ee0-d8cc-4acf-a391-b966069c75ee'),(31,199000.00,14,'aa714ee0-d8cc-4acf-a391-b966069c75ee'),(32,249000.00,5,'78264480-d30e-40a0-9d6d-3a5a7f39f196'),(33,199000.00,14,'78264480-d30e-40a0-9d6d-3a5a7f39f196'),(34,249000.00,5,'cbb90fe0-7530-496a-af18-e81e00414500'),(35,199000.00,14,'cbb90fe0-7530-496a-af18-e81e00414500'),(36,249000.00,5,'2b7bbf6d-a154-423d-b98b-645348621f13'),(37,199000.00,14,'2b7bbf6d-a154-423d-b98b-645348621f13'),(38,2500000.00,17,'f9c7f015-fb53-419e-bac3-a664593c77d4'),(39,2500000.00,17,'ef340df3-01bd-4e0d-8370-4dc12f753463'),(40,299000.00,16,'5d8d2769-ac96-41a3-8de3-12f7ca633738'),(41,199000.00,15,'c0d1d5a5-2cb4-49e1-933f-6cf6cc61cfe4'),(42,200000.00,8,'52d95c23-3851-4cc5-9503-98d42a5afb43'),(43,200000.00,11,'8d7e4f7f-87bb-4ad0-8001-dd2ef5abc77b'),(44,199000.00,1,'61a7f4a3-1a98-46ea-8cf5-dea81b2ba874'),(45,399000.00,3,'3d0b6cc7-6ac7-4ed1-a47e-21b54344df83'),(46,399000.00,3,'f9fa8e82-7205-4485-8b0f-62edffe39586'),(47,399000.00,3,'ff8cf10a-cfc1-438f-b26f-f83f0b591654'),(48,399000.00,3,'56deec1f-e15b-4df7-8b8c-7fe50ba0d2a0'),(49,199000.00,14,'09f3f5ef-282d-4162-9226-b475003ef557'),(50,200000.00,7,'6c8c4faf-cdfa-4790-a616-af98fbf793d6'),(51,200000.00,12,'a71e1095-ab3e-4f62-bb64-56a4648d7370'),(52,199000.00,1,'970fefb0-59d1-49fd-b5b7-1065c540e7d1'),(53,299000.00,16,'5a066607-f483-48c6-9e34-1fc450582a03'),(54,200000.00,11,'a9001118-766c-4870-915d-64b1546b43ea'),(55,200000.00,11,'7b9c977c-db18-425a-958e-d56dac1b9c7a'),(56,949000.00,9,'8ad2c42e-839a-489e-bbe8-1c5a2d5e22b8'),(57,199000.00,15,'be3db0e3-073a-403d-9c92-b17c39a7d367'),(58,2500000.00,17,'dbea9a77-1640-4588-9db0-0293ec254119'),(59,200000.00,10,'e6e2c377-6dc7-4063-a53b-bfb513679f7a'),(60,200000.00,10,'25eefd72-7b92-4b8f-9e7d-c00e2db797ab'),(61,299000.00,16,'25eefd72-7b92-4b8f-9e7d-c00e2db797ab'),(62,299000.00,16,'88c09fbf-7b8e-4b29-82df-d7a18fbe1d4a'),(63,299000.00,16,'533fc5e2-490b-4276-b9d1-91174fc4c96f'),(64,299000.00,16,'0d39dee2-6ec2-43e1-a364-213b07924eed');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','FAILED','PAID','PENDING') DEFAULT NULL,
  `total_amount` decimal(38,2) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('063d1b72-d96b-41d1-bea1-7c478e4e9216','2025-08-21 13:31:48.002372',NULL,NULL,'FAILED',599000.00,NULL,9),('09f3f5ef-282d-4162-9226-b475003ef557','2025-12-20 03:01:00.728361','2025-12-20 03:01:22.944263',NULL,'PAID',199000.00,NULL,4),('0d39dee2-6ec2-43e1-a364-213b07924eed','2026-01-25 19:28:27.610517','2026-01-25 19:28:48.128646',NULL,'PAID',299000.00,NULL,9),('13e991ef-09b0-4d5c-bdd9-7ccdbf5efb6e','2025-08-20 16:00:32.529547',NULL,NULL,'FAILED',399000.00,NULL,11),('1a4e08a7-7b24-4503-9f5b-45a18e15f221','2025-08-21 13:40:52.495339',NULL,NULL,'FAILED',399000.00,NULL,9),('25eefd72-7b92-4b8f-9e7d-c00e2db797ab','2026-01-25 19:04:51.968962',NULL,NULL,'PENDING',499000.00,NULL,9),('2b7bbf6d-a154-423d-b98b-645348621f13','2025-12-20 02:14:08.375023','2025-12-20 02:14:51.187421',NULL,'PAID',448000.00,NULL,9),('3d0b6cc7-6ac7-4ed1-a47e-21b54344df83','2025-12-20 02:51:54.408048',NULL,NULL,'PENDING',399000.00,NULL,4),('3ebb5825-d69a-43a5-bf9f-4f721df0ea27','2025-09-23 14:20:53.213640',NULL,NULL,'PENDING',249000.00,NULL,9),('41b98dce-20e0-4c22-a9e3-dceb29589e30','2025-08-20 16:25:14.069336','2025-08-20 16:25:57.130086',NULL,'PAID',200000.00,NULL,11),('52d95c23-3851-4cc5-9503-98d42a5afb43','2025-12-20 02:41:39.346786','2025-12-20 02:42:01.731315',NULL,'PAID',200000.00,NULL,4),('533fc5e2-490b-4276-b9d1-91174fc4c96f','2026-01-25 19:24:19.068848',NULL,NULL,'PENDING',299000.00,NULL,9),('56deec1f-e15b-4df7-8b8c-7fe50ba0d2a0','2025-12-20 02:53:54.943281','2025-12-20 02:55:12.597336',NULL,'PAID',399000.00,NULL,4),('59f74f1f-fb53-4911-b425-0d6aacec3056','2025-12-20 01:56:33.654780',NULL,NULL,'PENDING',199000.00,NULL,9),('5a066607-f483-48c6-9e34-1fc450582a03','2026-01-07 10:12:02.714988','2026-01-07 10:12:24.916163',NULL,'PAID',299000.00,NULL,26),('5abe3b82-0d96-479f-bc7f-02bb1cf8d2f5','2025-08-21 15:26:22.374773',NULL,NULL,'PENDING',199000.00,NULL,9),('5d8d2769-ac96-41a3-8de3-12f7ca633738','2025-12-20 02:27:28.256976','2025-12-20 02:27:53.484303',NULL,'PAID',299000.00,NULL,4),('5fbb5af2-159a-4f8f-9df5-dc536822a942','2025-08-21 21:04:49.068240','2025-08-21 21:05:38.465327',NULL,'PAID',449000.00,NULL,9),('61a7f4a3-1a98-46ea-8cf5-dea81b2ba874','2025-12-20 02:49:49.646134','2025-12-20 02:50:12.079603',NULL,'PAID',199000.00,NULL,4),('6c8c4faf-cdfa-4790-a616-af98fbf793d6','2025-12-20 03:05:34.674586','2025-12-20 03:06:01.303131',NULL,'PAID',200000.00,NULL,4),('70541ebd-0134-4546-bd7c-55d5a9ff3cf8','2025-08-20 16:07:25.639321',NULL,NULL,'PENDING',200000.00,NULL,11),('78264480-d30e-40a0-9d6d-3a5a7f39f196','2025-12-20 02:05:08.574425',NULL,NULL,'PENDING',448000.00,NULL,9),('7b9c977c-db18-425a-958e-d56dac1b9c7a','2026-01-07 10:15:28.787218','2026-01-07 10:15:47.556703',NULL,'PAID',200000.00,NULL,26),('83d5b81a-b0f7-4f04-a3fc-9c5eba8823fa','2025-08-21 14:53:29.684967',NULL,NULL,'PENDING',399000.00,NULL,9),('84ea4aa4-b566-48f4-a52b-b73dd2cdf7b4','2025-08-21 13:59:26.330181','2025-08-21 13:59:55.410075',NULL,'PAID',200000.00,NULL,9),('88c09fbf-7b8e-4b29-82df-d7a18fbe1d4a','2026-01-25 19:16:21.200577',NULL,NULL,'PENDING',299000.00,NULL,9),('8917d9d4-b4c7-489a-8d33-267a9586d83c','2025-08-21 13:58:17.167366','2025-08-21 13:58:41.003435',NULL,'PAID',399000.00,NULL,9),('8ad2c42e-839a-489e-bbe8-1c5a2d5e22b8','2026-01-07 10:16:43.311547','2026-01-07 10:17:10.756697',NULL,'PAID',949000.00,NULL,26),('8d7e4f7f-87bb-4ad0-8001-dd2ef5abc77b','2025-12-20 02:45:38.009236','2025-12-20 02:46:04.460706',NULL,'PAID',200000.00,NULL,4),('8e217824-6320-47b9-b059-9d229252c815','2025-10-23 16:42:56.609307',NULL,NULL,'PENDING',0.00,NULL,25),('970fefb0-59d1-49fd-b5b7-1065c540e7d1','2026-01-07 10:11:01.832719','2026-01-07 10:11:51.775476',NULL,'PAID',199000.00,NULL,26),('a71e1095-ab3e-4f62-bb64-56a4648d7370','2025-12-27 19:03:04.775434','2025-12-27 19:03:55.285387',NULL,'PAID',200000.00,NULL,4),('a9001118-766c-4870-915d-64b1546b43ea','2026-01-07 10:15:20.573628',NULL,NULL,'PENDING',200000.00,NULL,26),('aa714ee0-d8cc-4acf-a391-b966069c75ee','2025-12-20 02:03:29.715656',NULL,NULL,'PENDING',448000.00,NULL,9),('b84fee18-9ec8-493c-bda2-c3a1eea83bbe','2025-08-20 15:07:20.196647','2025-08-20 15:08:24.835348',NULL,'PAID',299000.00,NULL,11),('bcc56cd9-db0a-48f8-bf04-05fd7d09d136','2025-10-23 16:42:41.810566',NULL,NULL,'PENDING',0.00,NULL,25),('be3db0e3-073a-403d-9c92-b17c39a7d367','2026-01-07 10:17:28.674387','2026-01-07 10:17:46.659570',NULL,'PAID',199000.00,NULL,26),('c0d1d5a5-2cb4-49e1-933f-6cf6cc61cfe4','2025-12-20 02:29:57.447189','2025-12-20 02:30:20.802904',NULL,'PAID',199000.00,NULL,4),('cbb90fe0-7530-496a-af18-e81e00414500','2025-12-20 02:06:37.263496',NULL,NULL,'PENDING',448000.00,NULL,9),('d30a7855-e9ab-4966-ab6e-a97f651793de','2025-08-21 14:53:48.606232',NULL,NULL,'PENDING',399000.00,NULL,9),('d704240c-1213-4ce4-abfe-e1509ef37ff9','2025-09-23 14:20:25.736758',NULL,NULL,'FAILED',249000.00,NULL,9),('d710a58d-1fe2-4644-8fc7-e1be5792a081','2025-08-20 15:04:58.958126','2025-08-20 15:06:52.726868',NULL,'PAID',599000.00,NULL,11),('dbea9a77-1640-4588-9db0-0293ec254119','2026-01-07 10:26:51.384765','2026-01-07 10:27:15.351335',NULL,'PAID',2500000.00,NULL,26),('e3a4e4c6-f75b-4704-b1ef-fa533a78f02f','2025-10-23 16:21:18.624924','2025-10-23 16:24:01.557352',NULL,'PAID',200000.00,NULL,25),('e6e2c377-6dc7-4063-a53b-bfb513679f7a','2026-01-25 18:57:38.385213',NULL,NULL,'PENDING',200000.00,NULL,9),('e75dfe15-2a29-4cfc-a803-666175ea1e1a','2025-08-20 16:07:14.588265',NULL,NULL,'FAILED',200000.00,NULL,11),('ec9e83da-b4bc-4af2-b084-8f9c31110cbd','2025-12-20 01:57:05.957579',NULL,NULL,'PENDING',199000.00,NULL,9),('ef340df3-01bd-4e0d-8370-4dc12f753463','2025-12-20 02:23:32.716169','2025-12-20 02:23:54.085436',NULL,'PAID',2500000.00,NULL,4),('f35b9005-3c94-496d-b307-c3ad5fe5610c','2025-08-21 13:41:04.869075','2025-08-21 13:42:00.319225',NULL,'PAID',399000.00,NULL,9),('f9c7f015-fb53-419e-bac3-a664593c77d4','2025-12-20 02:20:47.347291','2025-12-20 02:21:19.520697',NULL,'PAID',2500000.00,NULL,9),('f9fa8e82-7205-4485-8b0f-62edffe39586','2025-12-20 02:52:09.115360',NULL,NULL,'PENDING',399000.00,NULL,4),('ff8cf10a-cfc1-438f-b26f-f83f0b591654','2025-12-20 02:52:27.624805',NULL,NULL,'PENDING',399000.00,NULL,4);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topic`
--

DROP TABLE IF EXISTS `topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topic` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK32ont8lv6suh4nf4j4f91vf01` (`creator_id`),
  CONSTRAINT `FK32ont8lv6suh4nf4j4f91vf01` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topic`
--

LOCK TABLES `topic` WRITE;
/*!40000 ALTER TABLE `topic` DISABLE KEYS */;
INSERT INTO `topic` VALUES (1,_binary '','2025-07-18 00:00:00.000000','Ngôn ngữ lập trình hướng đối tượng phổ biến.','Java',NULL,1),(2,_binary '','2025-07-18 00:00:00.000000','Ngôn ngữ lập trình mạnh mẽ cho phát triển web.','JavaScript',NULL,1),(3,_binary '','2025-07-18 00:00:00.000000','Ngôn ngữ lập trình đơn giản và dễ học.','Python',NULL,1),(4,_binary '','2025-07-18 00:00:00.000000','Ngôn ngữ lập trình mạnh cho hệ điều hành và nhúng.','C',NULL,1),(5,_binary '','2025-07-18 00:00:00.000000','Phiên bản hướng đối tượng mở rộng của C.','C++',NULL,1),(6,_binary '','2025-07-18 00:00:00.000000','Ngôn ngữ lập trình hiện đại được sử dụng trong phát triển Android.','Kotlin',NULL,1),(7,_binary '','2025-07-18 00:00:00.000000','Ngôn ngữ lập trình mới của Apple dành cho phát triển iOS.','Swift',NULL,1),(8,_binary '\0','2025-07-24 13:50:25.856338','Chủ đề về Html và Css','HTML','2025-07-24 13:50:40.662071',1);
/*!40000 ALTER TABLE `topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `fullname` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','LECTURER','MEMBER') DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `yob` date DEFAULT NULL,
  `id_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,NULL,'admin20@gmail.com',_binary '','Administrator',NULL,'$2a$10$0KhXrgG2MOb0iZ7VpgYs/uea.UBSKzTPuL7PQMEoVv8CdIy5Rpzx6',NULL,'LECTURER','admin',NULL,NULL),(4,'Phu Yen','https://freesvg.org/img/abstract-user-flat-3.png','laml3336@gmail.com',_binary '','Luong Gia Lam','MALE','L12345','0981527077','LECTURER','lamgia12','2004-12-20',NULL),(5,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','tinnguyen100904@gmail.com',_binary '','Nguyen Trung Tin',NULL,'$2a$10$PPTZcQkDwMit.uqqUY23Tu/NcgbkAXyOmGrNymlqi.3zOq/2FR3Re','0363464747','MEMBER','trungtin10','2004-12-04',NULL),(6,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','dat113@gmail.com',_binary '','Nguyen Thanh Dat',NULL,'$2a$10$ogHUXNq3Uz82FCFq4C.GzurjQ.DWL3e8jQDO0ZCFa.DUBnCzr5zF6','0987766554','MEMBER','dattran113',NULL,NULL),(7,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','con1@gmail.com',_binary '','Tran Quoc Cong',NULL,'$2a$10$PLnF3Qq6bBb3V5fv7.RDfe.NuMYzkwgIhN3QDjw1sF4.wRSnPdmE.','0987654321','MEMBER','congt1',NULL,NULL),(8,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','toan14@gmail.com',_binary '','Tấn Toàn',NULL,'$2a$10$/4Ob/BLiz0sS7XaNDnWnd.uId1Bdpd96SsPIt8e9LwoClYTa2OQ5O','0987654322','MEMBER','tantoan14','2003-01-21',NULL),(9,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','laml33366@gmail.com',_binary '','Gia Lâm','male','$2a$12$uXLAW8pXrDBHxvytnR7pTew.50s9emsp/5Z6Hpb14qB2VTdiSdLUe','0981527077','MEMBER','gialam13','2001-12-20',NULL),(10,'ĐăkLak','https://freesvg.org/img/abstract-user-flat-3.png','tin113@gmail.com',_binary '','Trung Tin','FEMALE','$2a$10$rN3GAt9acZ5s5kBSMYScf.IdW5o5aaeJ/lh1HMLgt3A9hrRhO0Owi','0987654322','LECTURER','tinnguyen113','2003-09-01',NULL),(11,NULL,'https://res.cloudinary.com/dhtjbtn1o/image/upload/v1755622818/upload/file_cxxphr.jpg','toantt1@gmail.com',_binary '','Tấn Toàn','male','$2a$10$ojWvNSF4OfpKLH0WYoCiJuXCkOtrnZ8CdAQ69HCeQtV34M1zaWV3O','0981527079','MEMBER','toantt11',NULL,NULL),(12,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','kieu1@g.com',_binary '','Kieu Luong',NULL,'$2a$10$OmyAEaJtS/qOoplt0co60eA/cM2dXDR3LUsGNwLqbzDL8LOKN/Kmy','0363464757','MEMBER','kieul123',NULL,NULL),(13,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','kieu12@g.com',_binary '','Kieu Luong',NULL,'$2a$10$8QuxAata6fMMCcTyzOD/yuGA2tVC4wrTdpsHE39YzR2KUxicnTequ','0363464757','MEMBER','kieul12',NULL,NULL),(14,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','kieu123@g.com',_binary '','Kieu Luong',NULL,'$2a$10$bPCGk1jlAdq4v7wGt9Ov2uH7KIgT5Isr1IAFxzbvnafw5h5QP7lUu','0363464757','MEMBER','kieul122',NULL,NULL),(16,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','codemaster379@gmail.com',_binary '','Jungle',NULL,'$2a$10$Pm/KyR./k7RF9pIFg4iEdOEyVliNmBb98j3COzRc4/WZuncvdQKuW','0987654344','MEMBER','jungle113',NULL,NULL),(17,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','lawliet12@gmail.com',_binary '','Jungle L',NULL,'$2a$10$2a5S4y/SbdjpG9V9ZHhQA./eHzB.hlBu3qJzwD6ANK1axI58Ic0.2','0987654344','MEMBER','Lawliet1',NULL,NULL),(18,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','lawliet1@gmail.com',_binary '','Jungle L',NULL,'$2a$10$qUIXjjOPerquOP9w8znkC.8Foj/A9LbBx6kfVoghQTVPSjDTxFdlC','0987654344','MEMBER','Lawliet12',NULL,NULL),(19,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','lawliet13@gmail.com',_binary '','Jungle L',NULL,'$2a$10$3IfjBhFHJs5tIUkHDxGl7eRLSrpl0StLs2MXjgO3DGPuCtUlJaC2i','0987654344','MEMBER','Lawliet123',NULL,NULL),(20,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','lawliet113@gmail.com',_binary '','Jungle L',NULL,'$2a$10$et92VAF4Q2KrD/4IV3H5Lu7wOst6oFxjQa5EvCjLsdSC3fkNdu6/C','0987654344','MEMBER','Lawliet13',NULL,NULL),(21,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','lawliet113@yobmail.com',_binary '','Jungle L',NULL,'$2a$10$MNavK2pMS4m/X1.XSDmFV..t4p6EASdVeEUhvk6ehxg1HQgcDjzmW','0987654344','MEMBER','Lawliet3',NULL,NULL),(22,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','codemaster3979@gmail.com',_binary '','Jungle L',NULL,'$2a$10$ZQIc94NpKG8kMO8TkqSdmOEXqj.WU8irW3mB0rKDY5jPrl0s3idSq','0987654344','MEMBER','Lawliet4',NULL,NULL),(24,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','lawliet999@yopmail.com',_binary '','Jungle L',NULL,'$2a$10$INzhQ4rnw0Fj.vu447Xw0O3v0qdzsVQmwV7AJtLooYqTVutOgpKay','0987654344','MEMBER','Lawliet5',NULL,NULL),(25,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','thinhnp@yobmail.com',_binary '','Phú Thịnh',NULL,'$2a$10$HUsAGRp4eM0.9EVFOIFUyOt18JAFhJmOUsxPWPctNybUNQlJjYN5y','0363464567','MEMBER','thinhnp12',NULL,NULL),(26,NULL,'https://freesvg.org/img/abstract-user-flat-3.png','ha22@gmail.com',_binary '','Ho Ngoc Ha',NULL,'$2a$10$Z8.MNrmpaGYkBLIZv9JrbOpW8MPt/5glhQI5/dAF2zrvbL7ls8fWO','0987654321','MEMBER','hongocha22','1997-02-12',NULL),(27,'','https://freesvg.org/img/abstract-user-flat-3.png','datcoder@gmail.com',_binary '','Nguyễn Thành Đạt','','$2a$10$4AQQmGgY1dHTekm.Rv9j3Oh8KqmaX0WO48z/IqRmvZp9vVO7YZgMm','0981432567','MEMBER','datnguyencoder1',NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watched_video`
--

DROP TABLE IF EXISTS `watched_video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `watched_video` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `watched` bit(1) NOT NULL,
  `watched_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  `video_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4bb08qc49l97jrqdt7ktj7kxp` (`user_id`),
  KEY `FK63fao5e6iwhtwge8cf0amiyuf` (`video_id`),
  CONSTRAINT `FK4bb08qc49l97jrqdt7ktj7kxp` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK63fao5e6iwhtwge8cf0amiyuf` FOREIGN KEY (`video_id`) REFERENCES `course_video` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watched_video`
--

LOCK TABLES `watched_video` WRITE;
/*!40000 ALTER TABLE `watched_video` DISABLE KEYS */;
INSERT INTO `watched_video` VALUES (7,_binary '','2025-07-24 12:36:52.400188',5,104),(8,_binary '','2025-07-24 12:36:59.244071',5,105),(9,_binary '','2025-07-24 12:37:00.205991',5,106),(10,_binary '','2025-07-24 12:37:03.899486',5,107),(11,_binary '','2025-07-24 12:37:04.932624',5,108),(12,_binary '','2025-07-24 12:37:05.851469',5,109),(13,_binary '','2025-07-24 13:45:45.491912',9,52),(14,_binary '','2025-07-24 13:46:13.096959',9,53),(15,_binary '','2025-07-24 13:46:18.806641',9,54),(16,_binary '','2025-07-24 13:46:19.755120',9,55),(17,_binary '','2025-07-24 13:46:20.609771',9,56),(18,_binary '','2025-07-24 13:46:21.597697',9,57),(19,_binary '','2025-07-24 13:46:28.576097',9,58),(20,_binary '','2025-07-24 13:46:34.572856',9,59),(21,_binary '','2025-08-20 15:20:54.986762',11,17),(22,_binary '','2025-08-20 15:20:55.498514',11,18),(23,_binary '','2025-08-20 15:20:56.009559',11,19),(24,_binary '','2025-08-20 15:20:57.216430',11,20),(25,_binary '','2025-08-20 15:20:57.567374',11,21),(26,_binary '','2025-08-20 15:20:58.046005',11,22),(27,_binary '','2025-08-20 15:20:59.482575',11,23),(28,_binary '','2025-08-20 15:20:59.884537',11,24),(29,_binary '','2025-08-30 22:04:07.381216',9,73),(30,_binary '\0','2025-08-30 22:04:28.073529',9,74),(31,_binary '','2025-10-23 16:24:24.995244',25,73),(32,_binary '','2025-10-23 16:24:27.358448',25,74),(33,_binary '','2025-10-23 16:24:45.807572',25,75),(34,_binary '','2025-10-23 16:24:51.769037',25,76),(35,_binary '','2025-10-23 16:25:04.392995',25,77),(36,_binary '','2025-10-23 16:25:05.177490',25,78),(37,_binary '','2026-01-23 17:15:34.871163',9,112),(38,_binary '','2026-01-23 17:17:37.619693',9,1);
/*!40000 ALTER TABLE `watched_video` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-31  9:41:22
