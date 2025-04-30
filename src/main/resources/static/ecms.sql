/*
 Navicat Premium Data Transfer

 Source Server         : asd
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : ecms

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 27/04/2025 10:32:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for customers
-- ----------------------------
DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers`  (
  `customer_id` int NOT NULL AUTO_INCREMENT COMMENT '顾客ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `registration_date` date NULL DEFAULT (curdate()) COMMENT '注册日期',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of customers
-- ----------------------------
INSERT INTO `customers` VALUES (4, '13800138000', 'customer1@example.com', '2024-01-05', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (5, '13912345678', 'customer2@test.com', '2024-02-18', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (6, '13698765432', NULL, '2024-03-22', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (7, '13800138001', 'customer3@example.com', '2024-01-10', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (8, '13912345679', 'customer4@example.com', '2024-02-20', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (9, '13923456780', 'customer5@example.com', '2024-04-10', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (10, '13654321098', 'customer6@example.com', '2024-05-15', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (15, '13711223344', 'customer11@example.com', '2024-10-10', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (16, '13599887766', 'customer12@example.com', '2024-11-20', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `customers` VALUES (17, '13956473829', 'customer13@example.com', '2024-12-15', '2025-04-02 17:47:31', '2025-04-02 17:47:31');

-- ----------------------------
-- Table structure for marketing_suggestions
-- ----------------------------
DROP TABLE IF EXISTS `marketing_suggestions`;
CREATE TABLE `marketing_suggestions`  (
  `suggestion_id` int NOT NULL AUTO_INCREMENT,
  `merchant_id` int NOT NULL,
  `suggestion_type` enum('promotion','pricing','advertising') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `generated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`suggestion_id`) USING BTREE,
  INDEX `merchant_id`(`merchant_id` ASC) USING BTREE,
  CONSTRAINT `marketing_suggestions_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of marketing_suggestions
-- ----------------------------
INSERT INTO `marketing_suggestions` VALUES (1, 3, 'promotion', 'Offer a 10% discount for orders above $100', '2025-04-02 12:32:45');
INSERT INTO `marketing_suggestions` VALUES (2, 4, 'advertising', 'Run a social media campaign targeting youth fashion', '2025-04-02 12:32:45');
INSERT INTO `marketing_suggestions` VALUES (7, 9, 'promotion', 'Launch a discount campaign for loyal customers with 20% off', '2025-04-02 12:32:45');
INSERT INTO `marketing_suggestions` VALUES (8, 10, 'advertising', 'Collaborate with influencers on Instagram to promote new arrivals', '2025-04-02 12:32:45');
INSERT INTO `marketing_suggestions` VALUES (9, 11, 'pricing', 'Offer a bundle deal with complementary products for a discount', '2025-04-02 12:32:45');

-- ----------------------------
-- Table structure for merchant_applications
-- ----------------------------
DROP TABLE IF EXISTS `merchant_applications`;
CREATE TABLE `merchant_applications`  (
  `application_id` int NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `merchant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商家名称',
  `contact_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系方式',
  `business_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经营范围',
  `application_date` datetime(6) NULL DEFAULT NULL,
  `status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '审核状态',
  `user_id` int NOT NULL COMMENT '用户ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商家资质文件路径',
  PRIMARY KEY (`application_id`) USING BTREE,
  INDEX `fk_merchant_application_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_merchant_application_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of merchant_applications
-- ----------------------------
INSERT INTO `merchant_applications` VALUES (4, '电子商城', 'contact@electronicstore.com', '电子产品', '2025-04-02 00:00:00.000000', 'approved', 3, '2025-04-02 17:47:31', '2025-04-16 18:47:20', '/licenses/health_license.pdf');
INSERT INTO `merchant_applications` VALUES (5, '时尚店铺', 'fashion@fashionstore.com', '服装鞋帽', '2025-04-02 00:00:00.000000', 'approved', 4, '2025-04-02 17:47:31', '2025-04-16 18:47:21', '/licenses/fashion_license.pdf');
INSERT INTO `merchant_applications` VALUES (6, '健康食品商店', 'info@healthstore.com', '食品饮料', '2025-04-02 00:00:00.000000', 'rejected', 5, '2025-04-02 17:47:31', '2025-04-16 18:47:22', '/licenses/electronic_license.pdf');
INSERT INTO `merchant_applications` VALUES (9, '23543', '45354', '453453', NULL, 'approved', 7, NULL, '2025-04-16 10:59:14', 'dsadasd');
INSERT INTO `merchant_applications` VALUES (10, '4253', '513', '513', '2025-04-16 11:03:14.337168', 'approved', 6, '2025-04-16 11:03:14', '2025-04-16 11:03:24', '531');
INSERT INTO `merchant_applications` VALUES (11, '3543', '3213', '5313', '2025-04-16 11:47:29.696573', 'approved', 8, '2025-04-16 11:47:30', '2025-04-16 11:47:41', '513153');
INSERT INTO `merchant_applications` VALUES (16, '3453', '543', '354', '2025-04-16 13:31:31.060956', 'approved', 9, '2025-04-16 13:31:31', '2025-04-16 13:31:42', '43');
INSERT INTO `merchant_applications` VALUES (17, '电子数码商店', 'contact@electronicsstore.com', '电子产品', '2025-04-17 00:00:00.000000', 'rejected', 3, '2025-04-17 17:47:31', '2025-04-23 08:59:33', '/licenses/digital_license.pdf');
INSERT INTO `merchant_applications` VALUES (18, '家居用品商店', 'info@homestore.com', '家具家居', '2025-04-17 00:00:00.000000', 'pending', 4, '2025-04-17 17:47:31', '2025-04-17 17:47:31', '/licenses/home_goods_license.pdf');
INSERT INTO `merchant_applications` VALUES (21, '453', '543', '453', '2025-04-23 08:59:45.163260', 'pending', 9, '2025-04-23 08:59:45', '2025-04-23 08:59:45', '453');
INSERT INTO `merchant_applications` VALUES (22, '453', '453', '453', '2025-04-23 09:01:18.900544', 'approved', 15, '2025-04-23 09:01:19', '2025-04-23 09:01:22', '453');
INSERT INTO `merchant_applications` VALUES (23, '453', '453', '453', '2025-04-23 09:02:39.053129', 'approved', 14, '2025-04-23 09:02:39', '2025-04-23 09:02:40', '534');

-- ----------------------------
-- Table structure for merchants
-- ----------------------------
DROP TABLE IF EXISTS `merchants`;
CREATE TABLE `merchants`  (
  `merchant_id` int NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `application_id` int NOT NULL COMMENT '入驻申请ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资质文件路径',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `contact_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `business_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `merchant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`merchant_id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `application_id`(`application_id` ASC) USING BTREE,
  CONSTRAINT `merchants_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `merchant_applications` (`application_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `merchants_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of merchants
-- ----------------------------
INSERT INTO `merchants` VALUES (3, 6, 3, '/licenses/electronic_license.pdf', 'active', '2025-04-02 17:47:31', '2025-04-16 16:49:24', NULL, NULL, NULL);
INSERT INTO `merchants` VALUES (4, 5, 4, '/licenses/fashion_license.pdf', 'inactive', '2025-04-02 17:47:31', '2025-04-16 16:49:26', NULL, NULL, NULL);
INSERT INTO `merchants` VALUES (5, 4, 5, '/licenses/health_license.pdf', 'active', '2025-04-02 17:47:31', '2025-04-16 16:49:29', NULL, NULL, NULL);
INSERT INTO `merchants` VALUES (8, 9, 7, 'dsadasd', 'active', '2025-04-16 10:59:14', '2025-04-16 10:59:14', '45354', '453453', '23543');
INSERT INTO `merchants` VALUES (9, 10, 6, '531', 'active', '2025-04-16 11:03:24', '2025-04-16 11:03:24', '513', '513', '4253');
INSERT INTO `merchants` VALUES (10, 11, 8, '513153', 'active', '2025-04-16 11:47:41', '2025-04-16 11:47:41', '3213', '5313', '3543');
INSERT INTO `merchants` VALUES (11, 16, 9, '43', 'active', '2025-04-16 13:31:42', '2025-04-16 13:31:42', '543', '354', '3453');
INSERT INTO `merchants` VALUES (23, 22, 15, '453', 'active', '2025-04-23 09:01:22', '2025-04-23 09:01:22', '453', '453', '453');
INSERT INTO `merchants` VALUES (25, 23, 14, '534', 'active', '2025-04-23 09:02:40', '2025-04-23 09:02:40', '453', '453', '453');

-- ----------------------------
-- Table structure for operation_logs
-- ----------------------------
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs`  (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `user_type` enum('platform','merchant') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` int NOT NULL,
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_action_time`(`action` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of operation_logs
-- ----------------------------
INSERT INTO `operation_logs` VALUES (1, 'merchant', 1, '商品上架', '上架新品：男士休闲衬衫', '2025-04-02 12:32:45');
INSERT INTO `operation_logs` VALUES (2, 'platform', 1, '审核通过', '批准商家入驻申请：时尚服饰旗舰店', '2025-04-02 12:32:45');
INSERT INTO `operation_logs` VALUES (3, 'platform', 1, '审核通过', '批准商家入驻申请：家居用品商店', '2025-04-02 12:33:45');
INSERT INTO `operation_logs` VALUES (4, 'merchant', 2, '商品上架', '上架新品：智能手表', '2025-04-02 12:34:00');
INSERT INTO `operation_logs` VALUES (8, 'platform', 1, '审核通过', '批准商家入驻申请：家具商场', '2025-04-02 12:33:45');
INSERT INTO `operation_logs` VALUES (9, 'merchant', 4, '商品上架', '上架新品：豪华床垫', '2025-04-02 12:34:30');
INSERT INTO `operation_logs` VALUES (10, 'platform', 1, '审核通过', '批准商家入驻申请：化妆品商店', '2025-04-02 12:35:00');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `item_id` int NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` int NOT NULL COMMENT '订单ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '购买数量',
  `unit_price` double NOT NULL,
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_order_product`(`order_id` ASC, `product_id` ASC) USING BTREE,
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 1, 1, 299);
INSERT INTO `order_items` VALUES (2, 1, 2, 1, 599);
INSERT INTO `order_items` VALUES (3, 3, 3, 2, 45.9);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `order_id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `merchant_id` int NOT NULL COMMENT '商家ID',
  `customer_id` int NOT NULL COMMENT '顾客ID',
  `order_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `total_amount` double NOT NULL,
  `order_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `product_id` int NOT NULL COMMENT '商品ID',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单区域',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_number`(`order_number` ASC) USING BTREE,
  INDEX `idx_merchant_order`(`merchant_id` ASC, `order_status` ASC) USING BTREE,
  INDEX `idx_customer`(`customer_id` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 3, 4, 'DD20240115001', 902, 'shipped', '2025-04-02 12:32:45', '2025-04-03 00:10:29', 1, '华东');
INSERT INTO `orders` VALUES (3, 5, 6, 'DD20240325003', 91.8, 'pending', '2025-04-02 12:32:45', '2025-04-02 18:09:03', 2, '华北');
INSERT INTO `orders` VALUES (4, 3, 4, '534', 0, 'completed', '2025-04-02 10:10:32', '2025-04-02 10:10:36', 3, '华南');
INSERT INTO `orders` VALUES (5, 4, 6, 'DD20240510001', 500, 'shipped', '2025-04-02 12:33:45', '2025-04-02 13:00:00', 4, '华东');
INSERT INTO `orders` VALUES (6, 5, 7, 'DD20240512001', 75.9, 'pending', '2025-04-02 12:33:45', '2025-04-02 13:05:00', 5, '华北');
INSERT INTO `orders` VALUES (11, 9, 15, 'DD20240517001', 250, 'shipped', '2025-04-02 12:35:45', '2025-04-02 14:10:00', 9, '华南');
INSERT INTO `orders` VALUES (12, 3, 4, 'sad', 1, 'pending', '2025-04-02 11:02:17', '2025-04-02 11:02:17', 1, '华东');
INSERT INTO `orders` VALUES (13, 3, 7, 'ORD2025022301', 299.8, 'completed', '2025-02-23 13:35:42', '2025-04-27 09:55:53', 5, '华北');
INSERT INTO `orders` VALUES (14, 3, 8, 'ORD2025022601', 149.9, 'completed', '2025-02-26 15:50:19', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (15, 3, 9, 'ORD2025030101', 149.9, 'completed', '2025-03-01 10:25:33', '2025-04-27 09:55:53', 5, '华南');
INSERT INTO `orders` VALUES (16, 3, 10, 'ORD2025030401', 299.8, 'completed', '2025-03-04 12:30:48', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (17, 3, 4, 'ORD2025030701', 149.9, 'completed', '2025-03-07 14:40:12', '2025-04-27 09:55:53', 5, '华北');
INSERT INTO `orders` VALUES (18, 3, 5, 'ORD2025030801', 299.8, 'completed', '2025-03-08 11:15:29', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (19, 3, 6, 'ORD2025030802', 299.8, 'completed', '2025-03-08 15:20:37', '2025-04-27 09:55:53', 5, '华南');
INSERT INTO `orders` VALUES (20, 3, 7, 'ORD2025031101', 149.9, 'completed', '2025-03-11 09:45:22', '2025-04-27 09:55:53', 5, '华北');
INSERT INTO `orders` VALUES (21, 3, 8, 'ORD2025031401', 149.9, 'completed', '2025-03-14 13:55:43', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (22, 3, 9, 'ORD2025031701', 299.8, 'completed', '2025-03-17 16:05:18', '2025-04-27 09:55:53', 5, '华南');
INSERT INTO `orders` VALUES (23, 3, 10, 'ORD2025032001', 149.9, 'completed', '2025-03-20 10:30:27', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (24, 3, 4, 'ORD2025032301', 149.9, 'completed', '2025-03-23 12:40:35', '2025-04-27 09:55:53', 5, '华北');
INSERT INTO `orders` VALUES (25, 3, 5, 'ORD2025032601', 299.8, 'completed', '2025-03-26 14:50:19', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (26, 3, 6, 'ORD2025032901', 149.9, 'completed', '2025-03-29 11:20:42', '2025-04-27 09:55:53', 5, '华南');
INSERT INTO `orders` VALUES (27, 3, 7, 'ORD2025040101', 149.9, 'completed', '2025-04-01 09:30:15', '2025-04-27 09:55:53', 5, '华北');
INSERT INTO `orders` VALUES (28, 3, 8, 'ORD2025040401', 299.8, 'completed', '2025-04-04 13:45:28', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (29, 3, 9, 'ORD2025040701', 149.9, 'completed', '2025-04-07 15:55:37', '2025-04-27 09:55:53', 5, '华南');
INSERT INTO `orders` VALUES (30, 3, 10, 'ORD2025041001', 299.8, 'completed', '2025-04-10 10:15:44', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (31, 3, 4, 'ORD2025041301', 149.9, 'completed', '2025-04-13 12:25:22', '2025-04-27 09:55:53', 5, '华北');
INSERT INTO `orders` VALUES (32, 3, 5, 'ORD2025041601', 149.9, 'completed', '2025-04-16 14:35:50', '2025-04-27 09:55:53', 5, '华东');
INSERT INTO `orders` VALUES (33, 3, 6, 'ORD2025041901', 299.8, 'completed', '2025-04-19 11:40:33', '2025-04-27 09:55:53', 5, '华南');
INSERT INTO `orders` VALUES (34, 3, 7, 'ORD2025042201', 149.9, 'completed', '2025-04-22 09:50:18', '2025-04-27 09:55:53', 5, '华北');
INSERT INTO `orders` VALUES (35, 3, 8, 'ORD2025042501', 149.9, 'completed', '2025-04-25 13:55:29', '2025-04-27 09:55:53', 5, '华东');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `product_id` int NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `merchant_id` int NOT NULL COMMENT '商家ID',
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` double NOT NULL,
  `stock_quantity` int NOT NULL DEFAULT 0 COMMENT '库存数量',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `idx_merchant`(`merchant_id` ASC) USING BTREE,
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 1, '男士休闲衬衫11', '纯棉商务休闲衬衫', 302, 102, 'active', NULL, NULL);
INSERT INTO `products` VALUES (2, 1, '女士针织连衣裙', '春季新款修身连衣裙', 599, 50, 'active', '2025-04-02 12:32:44', '2025-04-02 12:32:44');
INSERT INTO `products` VALUES (3, 2, '有机燕麦片', '无添加健康食品', 45.9, 200, 'inactive', NULL, NULL);
INSERT INTO `products` VALUES (5, 3, '家居沙发', '舒适家居沙发', 149.9, 50, 'active', '2025-04-02 12:32:45', '2025-04-02 12:32:45');
INSERT INTO `products` VALUES (9, 8, '豪华床垫', '高端记忆棉床垫', 899, 50, 'active', NULL, NULL);
INSERT INTO `products` VALUES (10, 9, '口红', '多色口红套装', 1499, 200, 'active', NULL, NULL);
INSERT INTO `products` VALUES (11, 10, '面霜', '滋润保湿面霜', 45.9, 100, 'inactive', NULL, NULL);

-- ----------------------------
-- Table structure for sales_data
-- ----------------------------
DROP TABLE IF EXISTS `sales_data`;
CREATE TABLE `sales_data`  (
  `sales_id` int NOT NULL AUTO_INCREMENT COMMENT '销售数据ID',
  `merchant_id` int NOT NULL COMMENT '商家ID',
  `sales_date` date NOT NULL COMMENT '统计日期',
  `total_orders` int NOT NULL COMMENT '总订单数',
  `avg_order_value` decimal(10, 2) NOT NULL,
  `region_orders` json NULL COMMENT '区域订单分布',
  `created_at` date NOT NULL,
  `region_sales` json NULL,
  `total_sales` decimal(14, 2) NOT NULL,
  PRIMARY KEY (`sales_id`) USING BTREE,
  UNIQUE INDEX `idx_merchant_date`(`merchant_id` ASC, `sales_date` ASC) USING BTREE,
  CONSTRAINT `sales_data_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sales_data
-- ----------------------------
INSERT INTO `sales_data` VALUES (1, 1, '2024-01-31', 50, 300.00, '{\"华东\": 10, \"华北\": 35, \"华南\": 5}', '2025-04-02', NULL, 0.00);
INSERT INTO `sales_data` VALUES (2, 1, '2024-02-28', 80, 275.00, '{\"华东\": 0, \"华北\": 70, \"华南\": 10}', '2025-04-02', NULL, 0.00);
INSERT INTO `sales_data` VALUES (3, 3, '2025-01-01', 12, 120.50, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1446.00);
INSERT INTO `sales_data` VALUES (4, 3, '2025-01-02', 8, 145.75, '{\"华东\": 3, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1166.00);
INSERT INTO `sales_data` VALUES (5, 3, '2025-01-03', 15, 132.20, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 1983.00);
INSERT INTO `sales_data` VALUES (6, 3, '2025-01-04', 10, 157.80, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1578.00);
INSERT INTO `sales_data` VALUES (7, 3, '2025-01-05', 7, 168.50, '{\"华东\": 3, \"华北\": 2, \"华南\": 2}', '2025-04-02', NULL, 1179.50);
INSERT INTO `sales_data` VALUES (8, 3, '2025-01-06', 14, 142.30, '{\"华东\": 5, \"华北\": 6, \"华南\": 3}', '2025-04-02', NULL, 1992.20);
INSERT INTO `sales_data` VALUES (9, 3, '2025-01-07', 18, 128.00, '{\"华东\": 7, \"华北\": 6, \"华南\": 5}', '2025-04-02', NULL, 2304.00);
INSERT INTO `sales_data` VALUES (10, 3, '2025-01-08', 11, 135.80, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1493.80);
INSERT INTO `sales_data` VALUES (11, 3, '2025-01-09', 9, 162.40, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1461.60);
INSERT INTO `sales_data` VALUES (12, 3, '2025-01-10', 16, 147.50, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2360.00);
INSERT INTO `sales_data` VALUES (13, 3, '2025-01-11', 13, 153.20, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 1991.60);
INSERT INTO `sales_data` VALUES (14, 3, '2025-01-12', 8, 142.80, '{\"华东\": 3, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1142.40);
INSERT INTO `sales_data` VALUES (15, 3, '2025-01-13', 12, 159.60, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1915.20);
INSERT INTO `sales_data` VALUES (16, 3, '2025-01-14', 17, 138.90, '{\"华东\": 7, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2361.30);
INSERT INTO `sales_data` VALUES (17, 3, '2025-01-15', 14, 145.20, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2032.80);
INSERT INTO `sales_data` VALUES (18, 3, '2025-01-16', 10, 167.50, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1675.00);
INSERT INTO `sales_data` VALUES (19, 3, '2025-01-17', 13, 152.30, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 1979.90);
INSERT INTO `sales_data` VALUES (20, 3, '2025-01-18', 11, 143.80, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1581.80);
INSERT INTO `sales_data` VALUES (21, 3, '2025-01-19', 9, 156.70, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1410.30);
INSERT INTO `sales_data` VALUES (22, 3, '2025-01-20', 15, 149.20, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2238.00);
INSERT INTO `sales_data` VALUES (23, 3, '2025-01-21', 12, 163.40, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1960.80);
INSERT INTO `sales_data` VALUES (24, 3, '2025-01-22', 8, 174.60, '{\"华东\": 3, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1396.80);
INSERT INTO `sales_data` VALUES (25, 3, '2025-01-23', 14, 158.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2216.20);
INSERT INTO `sales_data` VALUES (26, 3, '2025-01-24', 18, 141.70, '{\"华东\": 7, \"华北\": 7, \"华南\": 4}', '2025-04-02', NULL, 2550.60);
INSERT INTO `sales_data` VALUES (27, 3, '2025-01-25', 11, 152.90, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1681.90);
INSERT INTO `sales_data` VALUES (28, 3, '2025-01-26', 10, 166.50, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1665.00);
INSERT INTO `sales_data` VALUES (29, 3, '2025-01-27', 15, 148.70, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2230.50);
INSERT INTO `sales_data` VALUES (30, 3, '2025-01-28', 13, 157.30, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2044.90);
INSERT INTO `sales_data` VALUES (31, 3, '2025-01-29', 9, 169.80, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1528.20);
INSERT INTO `sales_data` VALUES (32, 3, '2025-01-30', 16, 145.20, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2323.20);
INSERT INTO `sales_data` VALUES (33, 3, '2025-01-31', 14, 153.40, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2147.60);
INSERT INTO `sales_data` VALUES (34, 3, '2025-02-01', 13, 125.50, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 1631.50);
INSERT INTO `sales_data` VALUES (35, 3, '2025-02-02', 9, 149.75, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1347.75);
INSERT INTO `sales_data` VALUES (36, 3, '2025-02-03', 16, 137.20, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2195.20);
INSERT INTO `sales_data` VALUES (37, 3, '2025-02-04', 11, 162.80, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1790.80);
INSERT INTO `sales_data` VALUES (38, 3, '2025-02-05', 8, 173.50, '{\"华东\": 3, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1388.00);
INSERT INTO `sales_data` VALUES (39, 3, '2025-02-06', 15, 147.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2209.50);
INSERT INTO `sales_data` VALUES (40, 3, '2025-02-07', 19, 133.00, '{\"华东\": 8, \"华北\": 7, \"华南\": 4}', '2025-04-02', NULL, 2527.00);
INSERT INTO `sales_data` VALUES (41, 3, '2025-02-08', 12, 140.80, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1689.60);
INSERT INTO `sales_data` VALUES (42, 3, '2025-02-09', 10, 167.40, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1674.00);
INSERT INTO `sales_data` VALUES (43, 3, '2025-02-10', 17, 152.50, '{\"华东\": 7, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2592.50);
INSERT INTO `sales_data` VALUES (44, 3, '2025-02-11', 14, 158.20, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2214.80);
INSERT INTO `sales_data` VALUES (45, 3, '2025-02-12', 9, 147.80, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1330.20);
INSERT INTO `sales_data` VALUES (46, 3, '2025-02-13', 13, 164.60, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2139.80);
INSERT INTO `sales_data` VALUES (47, 3, '2025-02-14', 22, 143.90, '{\"华东\": 9, \"华北\": 8, \"华南\": 5}', '2025-04-02', NULL, 3165.80);
INSERT INTO `sales_data` VALUES (48, 3, '2025-02-15', 15, 150.20, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2253.00);
INSERT INTO `sales_data` VALUES (49, 3, '2025-02-16', 11, 172.50, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1897.50);
INSERT INTO `sales_data` VALUES (50, 3, '2025-02-17', 14, 157.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2202.20);
INSERT INTO `sales_data` VALUES (51, 3, '2025-02-18', 12, 148.80, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1785.60);
INSERT INTO `sales_data` VALUES (52, 3, '2025-02-19', 10, 161.70, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1617.00);
INSERT INTO `sales_data` VALUES (53, 3, '2025-02-20', 16, 154.20, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2467.20);
INSERT INTO `sales_data` VALUES (54, 3, '2025-02-21', 13, 168.40, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2189.20);
INSERT INTO `sales_data` VALUES (55, 3, '2025-02-22', 9, 179.60, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1616.40);
INSERT INTO `sales_data` VALUES (56, 3, '2025-02-23', 15, 163.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2449.50);
INSERT INTO `sales_data` VALUES (57, 3, '2025-02-24', 19, 146.70, '{\"华东\": 8, \"华北\": 7, \"华南\": 4}', '2025-04-02', NULL, 2787.30);
INSERT INTO `sales_data` VALUES (58, 3, '2025-02-25', 12, 157.90, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1894.80);
INSERT INTO `sales_data` VALUES (59, 3, '2025-02-26', 11, 171.50, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1886.50);
INSERT INTO `sales_data` VALUES (60, 3, '2025-02-27', 16, 153.70, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2459.20);
INSERT INTO `sales_data` VALUES (61, 3, '2025-02-28', 14, 162.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2272.20);
INSERT INTO `sales_data` VALUES (62, 3, '2025-03-01', 13, 130.50, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 1696.50);
INSERT INTO `sales_data` VALUES (63, 3, '2025-03-02', 9, 154.75, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1392.75);
INSERT INTO `sales_data` VALUES (64, 3, '2025-03-03', 16, 142.20, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2275.20);
INSERT INTO `sales_data` VALUES (65, 3, '2025-03-04', 11, 167.80, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1845.80);
INSERT INTO `sales_data` VALUES (66, 3, '2025-03-05', 8, 178.50, '{\"华东\": 3, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1428.00);
INSERT INTO `sales_data` VALUES (67, 3, '2025-03-06', 15, 152.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2284.50);
INSERT INTO `sales_data` VALUES (68, 3, '2025-03-07', 19, 138.00, '{\"华东\": 8, \"华北\": 7, \"华南\": 4}', '2025-04-02', NULL, 2622.00);
INSERT INTO `sales_data` VALUES (69, 3, '2025-03-08', 17, 145.80, '{\"华东\": 7, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2478.60);
INSERT INTO `sales_data` VALUES (70, 3, '2025-03-09', 10, 172.40, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1724.00);
INSERT INTO `sales_data` VALUES (71, 3, '2025-03-10', 17, 157.50, '{\"华东\": 7, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2677.50);
INSERT INTO `sales_data` VALUES (72, 3, '2025-03-11', 14, 163.20, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2284.80);
INSERT INTO `sales_data` VALUES (73, 3, '2025-03-12', 9, 152.80, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1375.20);
INSERT INTO `sales_data` VALUES (74, 3, '2025-03-13', 13, 169.60, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2204.80);
INSERT INTO `sales_data` VALUES (75, 3, '2025-03-14', 18, 148.90, '{\"华东\": 7, \"华北\": 7, \"华南\": 4}', '2025-04-02', NULL, 2680.20);
INSERT INTO `sales_data` VALUES (76, 3, '2025-03-15', 15, 155.20, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2328.00);
INSERT INTO `sales_data` VALUES (77, 3, '2025-03-16', 11, 177.50, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1952.50);
INSERT INTO `sales_data` VALUES (78, 3, '2025-03-17', 14, 162.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2272.20);
INSERT INTO `sales_data` VALUES (79, 3, '2025-03-18', 12, 153.80, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1845.60);
INSERT INTO `sales_data` VALUES (80, 3, '2025-03-19', 10, 166.70, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1667.00);
INSERT INTO `sales_data` VALUES (81, 3, '2025-03-20', 16, 159.20, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2547.20);
INSERT INTO `sales_data` VALUES (82, 3, '2025-03-21', 13, 173.40, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2254.20);
INSERT INTO `sales_data` VALUES (83, 3, '2025-03-22', 9, 184.60, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-02', NULL, 1661.40);
INSERT INTO `sales_data` VALUES (84, 3, '2025-03-23', 15, 168.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2524.50);
INSERT INTO `sales_data` VALUES (85, 3, '2025-03-24', 19, 151.70, '{\"华东\": 8, \"华北\": 7, \"华南\": 4}', '2025-04-02', NULL, 2882.30);
INSERT INTO `sales_data` VALUES (86, 3, '2025-03-25', 12, 162.90, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1954.80);
INSERT INTO `sales_data` VALUES (87, 3, '2025-03-26', 11, 176.50, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-02', NULL, 1941.50);
INSERT INTO `sales_data` VALUES (88, 3, '2025-03-27', 16, 158.70, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2539.20);
INSERT INTO `sales_data` VALUES (89, 3, '2025-03-28', 14, 167.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 2342.20);
INSERT INTO `sales_data` VALUES (90, 3, '2025-03-29', 10, 179.80, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1798.00);
INSERT INTO `sales_data` VALUES (91, 3, '2025-03-30', 17, 155.20, '{\"华东\": 7, \"华北\": 6, \"华南\": 4}', '2025-04-02', NULL, 2638.40);
INSERT INTO `sales_data` VALUES (92, 3, '2025-03-31', 15, 163.40, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-02', NULL, 2451.00);
INSERT INTO `sales_data` VALUES (93, 3, '2025-04-01', 14, 135.50, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-02', NULL, 1897.00);
INSERT INTO `sales_data` VALUES (94, 3, '2025-04-02', 10, 159.75, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-02', NULL, 1597.50);
INSERT INTO `sales_data` VALUES (95, 3, '2025-04-03', 17, 147.20, '{\"华东\": 7, \"华北\": 6, \"华南\": 4}', '2025-04-03', NULL, 2502.40);
INSERT INTO `sales_data` VALUES (96, 3, '2025-04-04', 12, 172.80, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-04', NULL, 2073.60);
INSERT INTO `sales_data` VALUES (97, 3, '2025-04-05', 9, 183.50, '{\"华东\": 4, \"华北\": 3, \"华南\": 2}', '2025-04-05', NULL, 1651.50);
INSERT INTO `sales_data` VALUES (98, 3, '2025-04-06', 16, 157.30, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-06', NULL, 2516.80);
INSERT INTO `sales_data` VALUES (99, 3, '2025-04-07', 20, 143.00, '{\"华东\": 8, \"华北\": 7, \"华南\": 5}', '2025-04-07', NULL, 2860.00);
INSERT INTO `sales_data` VALUES (100, 3, '2025-04-08', 13, 150.80, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-08', NULL, 1960.40);
INSERT INTO `sales_data` VALUES (101, 3, '2025-04-09', 11, 177.40, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-09', NULL, 1951.40);
INSERT INTO `sales_data` VALUES (102, 3, '2025-04-10', 18, 162.50, '{\"华东\": 7, \"华北\": 7, \"华南\": 4}', '2025-04-10', NULL, 2925.00);
INSERT INTO `sales_data` VALUES (103, 3, '2025-04-11', 15, 168.20, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-11', NULL, 2523.00);
INSERT INTO `sales_data` VALUES (104, 3, '2025-04-12', 10, 157.80, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-12', NULL, 1578.00);
INSERT INTO `sales_data` VALUES (105, 3, '2025-04-13', 14, 174.60, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-13', NULL, 2444.40);
INSERT INTO `sales_data` VALUES (106, 3, '2025-04-14', 19, 153.90, '{\"华东\": 8, \"华北\": 7, \"华南\": 4}', '2025-04-14', NULL, 2924.10);
INSERT INTO `sales_data` VALUES (107, 3, '2025-04-15', 16, 160.20, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-15', NULL, 2563.20);
INSERT INTO `sales_data` VALUES (108, 3, '2025-04-16', 12, 182.50, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-16', NULL, 2190.00);
INSERT INTO `sales_data` VALUES (109, 3, '2025-04-17', 15, 167.30, '{\"华东\": 6, \"华北\": 5, \"华南\": 4}', '2025-04-17', NULL, 2509.50);
INSERT INTO `sales_data` VALUES (110, 3, '2025-04-18', 13, 158.80, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-18', NULL, 2064.40);
INSERT INTO `sales_data` VALUES (111, 3, '2025-04-19', 11, 171.70, '{\"华东\": 4, \"华北\": 4, \"华南\": 3}', '2025-04-19', NULL, 1888.70);
INSERT INTO `sales_data` VALUES (112, 3, '2025-04-20', 17, 164.20, '{\"华东\": 7, \"华北\": 6, \"华南\": 4}', '2025-04-20', NULL, 2791.40);
INSERT INTO `sales_data` VALUES (113, 3, '2025-04-21', 14, 178.40, '{\"华东\": 6, \"华北\": 5, \"华南\": 3}', '2025-04-21', NULL, 2497.60);
INSERT INTO `sales_data` VALUES (114, 3, '2025-04-22', 10, 189.60, '{\"华东\": 4, \"华北\": 4, \"华南\": 2}', '2025-04-22', NULL, 1896.00);
INSERT INTO `sales_data` VALUES (115, 3, '2025-04-23', 16, 173.30, '{\"华东\": 6, \"华北\": 6, \"华南\": 4}', '2025-04-23', NULL, 2772.80);
INSERT INTO `sales_data` VALUES (116, 3, '2025-04-24', 20, 156.70, '{\"华东\": 8, \"华北\": 7, \"华南\": 5}', '2025-04-24', NULL, 3134.00);
INSERT INTO `sales_data` VALUES (117, 3, '2025-04-25', 13, 167.90, '{\"华东\": 5, \"华北\": 5, \"华南\": 3}', '2025-04-25', NULL, 2182.70);
INSERT INTO `sales_data` VALUES (118, 3, '2025-04-26', 12, 181.50, '{\"华东\": 5, \"华北\": 4, \"华南\": 3}', '2025-04-26', NULL, 2178.00);

-- ----------------------------
-- Table structure for sales_forecasts
-- ----------------------------
DROP TABLE IF EXISTS `sales_forecasts`;
CREATE TABLE `sales_forecasts`  (
  `forecast_id` int NOT NULL AUTO_INCREMENT COMMENT '预测ID',
  `merchant_id` int NOT NULL COMMENT '商家ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `forecast_date` datetime(6) NULL DEFAULT NULL,
  `predicted_sales` decimal(12, 2) NOT NULL COMMENT '预测销量',
  `confidence_interval` json NULL COMMENT '置信区间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`forecast_id`) USING BTREE,
  INDEX `merchant_id`(`merchant_id` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_forecast_date`(`forecast_date` ASC) USING BTREE,
  CONSTRAINT `sales_forecasts_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sales_forecasts_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1755 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sales_forecasts
-- ----------------------------
INSERT INTO `sales_forecasts` VALUES (1575, 8, 9, '2025-04-27 16:00:00.000000', 249.36, '{\"lower\": 211.96, \"upper\": 286.76}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1576, 8, 9, '2025-04-28 16:00:00.000000', 290.73, '{\"lower\": 247.12, \"upper\": 334.34}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1577, 8, 9, '2025-04-29 16:00:00.000000', 297.62, '{\"lower\": 252.98, \"upper\": 342.26}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1578, 8, 9, '2025-04-30 16:00:00.000000', 265.10, '{\"lower\": 225.34, \"upper\": 304.87}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1579, 8, 9, '2025-05-01 16:00:00.000000', 230.46, '{\"lower\": 195.89, \"upper\": 265.03}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1580, 8, 9, '2025-05-02 16:00:00.000000', 212.42, '{\"lower\": 180.56, \"upper\": 244.28}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1581, 8, 9, '2025-05-03 16:00:00.000000', 203.60, '{\"lower\": 173.06, \"upper\": 234.14}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1582, 8, 9, '2025-05-04 16:00:00.000000', 256.60, '{\"lower\": 218.11, \"upper\": 295.09}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1583, 8, 9, '2025-05-05 16:00:00.000000', 300.03, '{\"lower\": 255.03, \"upper\": 345.04}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1584, 8, 9, '2025-05-06 16:00:00.000000', 294.48, '{\"lower\": 250.31, \"upper\": 338.65}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1585, 8, 9, '2025-05-07 16:00:00.000000', 274.98, '{\"lower\": 233.73, \"upper\": 316.22}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1586, 8, 9, '2025-05-08 16:00:00.000000', 229.32, '{\"lower\": 194.92, \"upper\": 263.71}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1587, 8, 9, '2025-05-09 16:00:00.000000', 200.37, '{\"lower\": 170.32, \"upper\": 230.43}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1588, 8, 9, '2025-05-10 16:00:00.000000', 205.24, '{\"lower\": 174.45, \"upper\": 236.03}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1589, 8, 9, '2025-05-11 16:00:00.000000', 245.37, '{\"lower\": 208.57, \"upper\": 282.18}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1590, 8, 9, '2025-05-12 16:00:00.000000', 290.64, '{\"lower\": 247.04, \"upper\": 334.23}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1591, 8, 9, '2025-05-13 16:00:00.000000', 300.75, '{\"lower\": 255.64, \"upper\": 345.86}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1592, 8, 9, '2025-05-14 16:00:00.000000', 279.36, '{\"lower\": 237.45, \"upper\": 321.26}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1593, 8, 9, '2025-05-15 16:00:00.000000', 239.02, '{\"lower\": 203.17, \"upper\": 274.87}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1594, 8, 9, '2025-05-16 16:00:00.000000', 213.89, '{\"lower\": 181.81, \"upper\": 245.98}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1595, 8, 9, '2025-05-17 16:00:00.000000', 217.93, '{\"lower\": 185.24, \"upper\": 250.61}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1596, 8, 9, '2025-05-18 16:00:00.000000', 252.11, '{\"lower\": 214.3, \"upper\": 289.93}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1597, 8, 9, '2025-05-19 16:00:00.000000', 309.58, '{\"lower\": 263.15, \"upper\": 356.02}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1598, 8, 9, '2025-05-20 16:00:00.000000', 302.07, '{\"lower\": 256.76, \"upper\": 347.38}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1599, 8, 9, '2025-05-21 16:00:00.000000', 293.69, '{\"lower\": 249.63, \"upper\": 337.74}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1600, 8, 9, '2025-05-22 16:00:00.000000', 232.18, '{\"lower\": 197.35, \"upper\": 267.0}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1601, 8, 9, '2025-05-23 16:00:00.000000', 216.99, '{\"lower\": 184.44, \"upper\": 249.54}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1602, 8, 9, '2025-05-24 16:00:00.000000', 219.14, '{\"lower\": 186.27, \"upper\": 252.01}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1603, 8, 9, '2025-05-25 16:00:00.000000', 268.99, '{\"lower\": 228.64, \"upper\": 309.33}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1604, 8, 9, '2025-05-26 16:00:00.000000', 306.70, '{\"lower\": 260.69, \"upper\": 352.7}', '2025-04-27 01:57:46');
INSERT INTO `sales_forecasts` VALUES (1695, 9, 10, '2025-04-27 16:00:00.000000', 253.58, '{\"lower\": 215.54, \"upper\": 291.62}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1696, 9, 10, '2025-04-28 16:00:00.000000', 258.65, '{\"lower\": 219.85, \"upper\": 297.45}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1697, 9, 10, '2025-04-29 16:00:00.000000', 257.72, '{\"lower\": 219.06, \"upper\": 296.37}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1698, 9, 10, '2025-04-30 16:00:00.000000', 262.96, '{\"lower\": 223.52, \"upper\": 302.4}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1699, 9, 10, '2025-05-01 16:00:00.000000', 258.51, '{\"lower\": 219.74, \"upper\": 297.29}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1700, 9, 10, '2025-05-02 16:00:00.000000', 247.48, '{\"lower\": 210.35, \"upper\": 284.6}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1701, 9, 10, '2025-05-03 16:00:00.000000', 256.54, '{\"lower\": 218.06, \"upper\": 295.02}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1702, 9, 10, '2025-05-04 16:00:00.000000', 255.98, '{\"lower\": 217.58, \"upper\": 294.38}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1703, 9, 10, '2025-05-05 16:00:00.000000', 268.33, '{\"lower\": 228.08, \"upper\": 308.58}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1704, 9, 10, '2025-05-06 16:00:00.000000', 269.27, '{\"lower\": 228.88, \"upper\": 309.66}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1705, 9, 10, '2025-05-07 16:00:00.000000', 265.56, '{\"lower\": 225.73, \"upper\": 305.4}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1706, 9, 10, '2025-05-08 16:00:00.000000', 266.50, '{\"lower\": 226.52, \"upper\": 306.47}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1707, 9, 10, '2025-05-09 16:00:00.000000', 279.49, '{\"lower\": 237.56, \"upper\": 321.41}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1708, 9, 10, '2025-05-10 16:00:00.000000', 293.22, '{\"lower\": 249.24, \"upper\": 337.2}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1709, 9, 10, '2025-05-11 16:00:00.000000', 296.84, '{\"lower\": 252.32, \"upper\": 341.37}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1710, 9, 10, '2025-05-12 16:00:00.000000', 291.23, '{\"lower\": 247.55, \"upper\": 334.92}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1711, 9, 10, '2025-05-13 16:00:00.000000', 296.74, '{\"lower\": 252.23, \"upper\": 341.25}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1712, 9, 10, '2025-05-14 16:00:00.000000', 283.10, '{\"lower\": 240.64, \"upper\": 325.57}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1713, 9, 10, '2025-05-15 16:00:00.000000', 284.31, '{\"lower\": 241.67, \"upper\": 326.96}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1714, 9, 10, '2025-05-16 16:00:00.000000', 288.42, '{\"lower\": 245.16, \"upper\": 331.68}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1715, 9, 10, '2025-05-17 16:00:00.000000', 287.55, '{\"lower\": 244.42, \"upper\": 330.68}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1716, 9, 10, '2025-05-18 16:00:00.000000', 283.17, '{\"lower\": 240.7, \"upper\": 325.65}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1717, 9, 10, '2025-05-19 16:00:00.000000', 293.90, '{\"lower\": 249.82, \"upper\": 337.99}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1718, 9, 10, '2025-05-20 16:00:00.000000', 285.74, '{\"lower\": 242.88, \"upper\": 328.61}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1719, 9, 10, '2025-05-21 16:00:00.000000', 293.24, '{\"lower\": 249.26, \"upper\": 337.23}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1720, 9, 10, '2025-05-22 16:00:00.000000', 283.41, '{\"lower\": 240.9, \"upper\": 325.92}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1721, 9, 10, '2025-05-23 16:00:00.000000', 296.32, '{\"lower\": 251.87, \"upper\": 340.77}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1722, 9, 10, '2025-05-24 16:00:00.000000', 288.64, '{\"lower\": 245.34, \"upper\": 331.93}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1723, 9, 10, '2025-05-25 16:00:00.000000', 281.72, '{\"lower\": 239.46, \"upper\": 323.97}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1724, 9, 10, '2025-05-26 16:00:00.000000', 291.05, '{\"lower\": 247.4, \"upper\": 334.71}', '2025-04-27 02:11:26');
INSERT INTO `sales_forecasts` VALUES (1725, 3, 5, '2025-04-27 16:00:00.000000', 2179.09, '{\"lower\": 1852.23, \"upper\": 2505.95}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1726, 3, 5, '2025-04-28 16:00:00.000000', 2179.01, '{\"lower\": 1852.16, \"upper\": 2505.87}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1727, 3, 5, '2025-04-29 16:00:00.000000', 2178.98, '{\"lower\": 1852.13, \"upper\": 2505.83}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1728, 3, 5, '2025-04-30 16:00:00.000000', 2178.95, '{\"lower\": 1852.11, \"upper\": 2505.79}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1729, 3, 5, '2025-05-01 16:00:00.000000', 2178.93, '{\"lower\": 1852.09, \"upper\": 2505.77}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1730, 3, 5, '2025-05-02 16:00:00.000000', 2178.92, '{\"lower\": 1852.08, \"upper\": 2505.75}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1731, 3, 5, '2025-05-03 16:00:00.000000', 2178.91, '{\"lower\": 1852.07, \"upper\": 2505.74}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1732, 3, 5, '2025-05-04 16:00:00.000000', 2178.90, '{\"lower\": 1852.07, \"upper\": 2505.74}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1733, 3, 5, '2025-05-05 16:00:00.000000', 2178.90, '{\"lower\": 1852.06, \"upper\": 2505.73}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1734, 3, 5, '2025-05-06 16:00:00.000000', 2178.89, '{\"lower\": 1852.06, \"upper\": 2505.73}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1735, 3, 5, '2025-05-07 16:00:00.000000', 2178.89, '{\"lower\": 1852.06, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1736, 3, 5, '2025-05-08 16:00:00.000000', 2178.89, '{\"lower\": 1852.06, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1737, 3, 5, '2025-05-09 16:00:00.000000', 2178.89, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1738, 3, 5, '2025-05-10 16:00:00.000000', 2178.89, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1739, 3, 5, '2025-05-11 16:00:00.000000', 2178.89, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1740, 3, 5, '2025-05-12 16:00:00.000000', 2178.89, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1741, 3, 5, '2025-05-13 16:00:00.000000', 2178.89, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1742, 3, 5, '2025-05-14 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1743, 3, 5, '2025-05-15 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1744, 3, 5, '2025-05-16 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1745, 3, 5, '2025-05-17 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1746, 3, 5, '2025-05-18 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1747, 3, 5, '2025-05-19 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1748, 3, 5, '2025-05-20 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1749, 3, 5, '2025-05-21 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1750, 3, 5, '2025-05-22 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1751, 3, 5, '2025-05-23 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1752, 3, 5, '2025-05-24 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1753, 3, 5, '2025-05-25 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');
INSERT INTO `sales_forecasts` VALUES (1754, 3, 5, '2025-05-26 16:00:00.000000', 2178.88, '{\"lower\": 1852.05, \"upper\": 2505.72}', '2025-04-27 02:11:45');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin1', 'password123', 'admin', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `users` VALUES (2, 'admin2', 'password123', 'admin', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `users` VALUES (3, 'merchant1', 'merchantpass', 'merchant', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `users` VALUES (4, 'merchant2', 'merchantpass', 'merchant', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `users` VALUES (5, 'merchant3', 'merchantpass', 'merchant', '2025-04-02 17:47:31', '2025-04-02 17:47:31');
INSERT INTO `users` VALUES (6, 'mer4', '12346', 'merchant', '2025-04-16 18:49:04', '2025-04-16 18:49:04');
INSERT INTO `users` VALUES (7, 'mer5', '123456', 'merchant', '2025-04-16 18:49:17', '2025-04-16 18:49:17');
INSERT INTO `users` VALUES (8, '531', '123456', 'merchant', '2025-04-16 19:47:18', '2025-04-16 19:47:18');
INSERT INTO `users` VALUES (9, '524', '5452', 'merchant', '2025-04-16 21:31:19', '2025-04-16 21:31:19');
INSERT INTO `users` VALUES (10, 'admin3', 'adminpass', 'admin', '2025-04-17 18:47:31', '2025-04-17 18:47:31');
INSERT INTO `users` VALUES (11, 'merchant4', 'merchantpass', 'merchant', '2025-04-17 18:47:31', '2025-04-17 18:47:31');
INSERT INTO `users` VALUES (14, 'admin5', 'adminpass', 'admin', '2025-04-20 18:47:31', '2025-04-20 18:47:31');
INSERT INTO `users` VALUES (15, 'merchant6', 'merchantpass', 'merchant', '2025-04-20 18:47:31', '2025-04-20 18:47:31');

-- ----------------------------
-- Table structure for visualization_data
-- ----------------------------
DROP TABLE IF EXISTS `visualization_data`;
CREATE TABLE `visualization_data`  (
  `data_id` int NOT NULL AUTO_INCREMENT COMMENT '数据ID',
  `merchant_id` int NOT NULL COMMENT '商家ID',
  `chart_type` enum('line','bar','pie','heatmap') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `data_config` json NOT NULL COMMENT '图表配置',
  `data_snapshot` json NOT NULL COMMENT '数据快照',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`data_id`) USING BTREE,
  INDEX `idx_merchant_chart`(`merchant_id` ASC, `chart_type` ASC) USING BTREE,
  CONSTRAINT `visualization_data_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`merchant_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of visualization_data
-- ----------------------------
INSERT INTO `visualization_data` VALUES (1, 1, 'line', '{\"title\": \"月度销售趋势\", \"xAxis\": \"月份\", \"yAxis\": \"销售额\"}', '{\"2024-01\": 15000, \"2024-02\": 22000}', '2025-04-02 12:32:45');
INSERT INTO `visualization_data` VALUES (2, 1, 'pie', '{\"title\": \"区域销售分布\", \"radius\": \"60%\"}', '{\"华东\": 55, \"华北\": 35, \"华南\": 10}', '2025-04-02 12:32:45');
INSERT INTO `visualization_data` VALUES (3, 5, 'line', '{\"title\": \"2024年订单趋势\", \"xAxis\": \"日期\", \"yAxis\": \"订单数量\"}', '{\"2024-01-01\": 50, \"2024-02-01\": 75, \"2024-03-01\": 60}', '2025-04-02 12:32:45');
INSERT INTO `visualization_data` VALUES (4, 3, 'bar', '{\"title\": \"2024年销售数据\", \"xAxis\": \"月份\", \"yAxis\": \"销售额\"}', '{\"2024-01\": 15000, \"2024-02\": 22000, \"2024-03\": 18000}', '2025-04-02 12:32:45');
INSERT INTO `visualization_data` VALUES (5, 3, 'line', '{\"title\": \"每月销售趋势\", \"xAxis\": \"时间\", \"yAxis\": \"销售额\"}', '{\"xAxis\": [\"2025-04\"], \"series\": [903.0]}', '2025-04-20 15:33:12');
INSERT INTO `visualization_data` VALUES (6, 3, 'pie', '{\"title\": \"产品销售分布\", \"radius\": \"60%\"}', '{\"data\": [{\"name\": \"未知产品\", \"value\": 903.0}]}', '2025-04-20 15:33:12');
INSERT INTO `visualization_data` VALUES (7, 3, 'bar', '{\"title\": \"区域销售分布\"}', '{\"values\": [0.0, 903.0], \"regions\": [\"华南\", \"华东\"]}', '2025-04-20 15:33:12');
INSERT INTO `visualization_data` VALUES (8, 10, 'bar', '{\"title\": \"季度销售数据\", \"xAxis\": \"季度\", \"yAxis\": \"销售额\"}', '{\"Q1\": 30000, \"Q2\": 45000, \"Q3\": 35000, \"Q4\": 50000}', '2025-04-02 12:32:45');
INSERT INTO `visualization_data` VALUES (9, 11, 'heatmap', '{\"title\": \"各月销售波动\", \"xAxis\": \"月份\", \"yAxis\": \"波动率\"}', '{\"2024-01\": 0.15, \"2024-02\": 0.18, \"2024-03\": 0.12}', '2025-04-02 12:32:45');

SET FOREIGN_KEY_CHECKS = 1;
