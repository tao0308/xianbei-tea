-- =============================================
-- 仙贝奶茶店 · 数据库初始化脚本
-- 用法：在 MySQL 中执行此文件
-- =============================================

-- 1. 创建数据库（如果尚未创建）
CREATE DATABASE IF NOT EXISTS xianbei_tea
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE xianbei_tea;

-- =============================================
-- 2. 数据表（JPA 的 ddl-auto: update 会自动创建，
--    但如果想手动创建，可以取消下面的注释）
-- =============================================

-- CREATE TABLE sys_user (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(50) NOT NULL UNIQUE,
--     password VARCHAR(255) NOT NULL,
--     real_name VARCHAR(50) NOT NULL,
--     phone VARCHAR(20) NOT NULL,
--     role VARCHAR(20) NOT NULL DEFAULT 'STAFF',
--     status TINYINT NOT NULL DEFAULT 1,
--     created_at DATETIME NOT NULL,
--     updated_at DATETIME NOT NULL
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- CREATE TABLE category (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(50) NOT NULL,
--     sort INT NOT NULL DEFAULT 0,
--     status TINYINT NOT NULL DEFAULT 1,
--     created_at DATETIME NOT NULL
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- CREATE TABLE product (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     category_id BIGINT NOT NULL,
--     name VARCHAR(100) NOT NULL,
--     price DECIMAL(10,2) NOT NULL,
--     description VARCHAR(500),
--     image_url VARCHAR(255),
--     status TINYINT NOT NULL DEFAULT 1,
--     created_at DATETIME NOT NULL,
--     updated_at DATETIME NOT NULL,
--     FOREIGN KEY (category_id) REFERENCES category(id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 3. 插入初始店长账号
--    （密码是 admin123 的 BCrypt 加密结果）
--    账号: admin      密码: admin123
-- =============================================

INSERT INTO sys_user (username, password, real_name, phone, role, status, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- admin123
    '店长',
    '13800000000',
    'MANAGER',
    1,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE username = username;
-- 如果重复执行，不会重复插入
