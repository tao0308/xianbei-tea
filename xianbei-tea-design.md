# 仙贝奶茶店后台管理系统 — 设计文档

> 日期：2026-05-24
> 技术栈：Vue 3 + Element Plus + Spring Boot 3.x + MySQL 8.x + JWT

## 一、项目概述

前后端分离的奶茶店后台管理系统，提供员工管理、饮品分类管理、饮品管理功能。店长拥有完整管理权限，员工可自行注册并由店长审核管理。

## 二、系统架构

```
Vue 3 + Element Plus ──REST/JWT──► Spring Boot 3.x ──► MySQL 8.x
```

- 前端：Vue 3 + Vue Router + Pinia + Element Plus
- 后端：Spring Boot 3.x + Spring Security + JWT + JPA/MyBatis + MySQL 8.x
- 认证：JWT Token，后端拦截器校验，角色级权限控制

## 三、角色与权限

| 角色 | 可访问模块 | 说明 |
|------|-----------|------|
| `MANAGER`（店长） | 全部 | 初始内置一个 admin 账号，不可注册 |
| `STAFF`（员工） | 工作台、分类管理、饮品管理 | 注册时默认为员工，由店长管理账号 |

## 四、数据库设计

### 4.1 sys_user（用户表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 登录用户名 |
| password | VARCHAR(255) | NOT NULL | BCrypt加密密码 |
| real_name | VARCHAR(50) | NOT NULL | 真实姓名 |
| phone | VARCHAR(20) | NOT NULL | 手机号 |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'STAFF' | 角色：MANAGER / STAFF |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态：1启用 / 0禁用 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

### 4.2 category（饮品分类表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 分类ID |
| name | VARCHAR(50) | NOT NULL | 分类名称 |
| sort | INT | DEFAULT 0 | 排序号 |
| status | TINYINT | DEFAULT 1 | 状态：1显示 / 0隐藏 |
| created_at | DATETIME | NOT NULL | 创建时间 |

### 4.3 product（饮品表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 饮品ID |
| category_id | BIGINT | FK → category.id | 所属分类 |
| name | VARCHAR(100) | NOT NULL | 饮品名称 |
| price | DECIMAL(10,2) | NOT NULL | 价格 |
| description | VARCHAR(500) | 可空 | 描述信息 |
| image_url | VARCHAR(255) | 可空 | 图片地址 |
| status | TINYINT | DEFAULT 1 | 状态：1上架 / 0下架 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

## 五、API 接口

### 5.1 公共接口（无需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录，返回 JWT Token |
| POST | /api/auth/register | 员工注册（角色默认为 STAFF） |
| POST | /api/auth/forgot-password | 验证用户名和手机号 |
| POST | /api/auth/reset-password | 重置密码 |

### 5.2 员工管理（仅店长）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users | 获取员工列表 |
| POST | /api/users | 新增员工 |
| PUT | /api/users/{id} | 编辑员工信息 |
| PUT | /api/users/{id}/status | 启用/禁用员工 |
| PUT | /api/users/{id}/password | 店长重置员工密码 |
| DELETE | /api/users/{id} | 删除员工 |

### 5.3 分类管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/categories | 获取分类列表 |
| POST | /api/categories | 新增分类 |
| PUT | /api/categories/{id} | 编辑分类 |
| DELETE | /api/categories/{id} | 删除分类（有饮品关联则禁止删除） |

### 5.4 饮品管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/products | 获取饮品列表（支持按分类筛选） |
| POST | /api/products | 新增饮品 |
| PUT | /api/products/{id} | 编辑饮品信息 |
| PUT | /api/products/{id}/status | 上架/下架饮品 |
| DELETE | /api/products/{id} | 删除饮品 |

### 5.5 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 六、前端页面与路由

| 路径 | 页面 | 角色 |
|------|------|------|
| /login | 登录页 | 所有人 |
| /register | 注册页 | 所有人 |
| /forgot-password | 忘记密码页 | 所有人 |
| /dashboard | 工作台首页 | 所有人 |
| /users | 员工管理 | 仅 MANAGER |
| /categories | 分类管理 | 所有人 |
| /products | 饮品管理 | 所有人 |

### 页面说明

**登录页**：用户名 + 密码输入 + 登录按钮，底部提供"注册账号"和"忘记密码"链接

**注册页**：用户名、姓名、手机号、密码、确认密码，提交后账号为员工角色，可直接登录

**忘记密码页**：输入用户名 + 手机号验证身份 → 验证通过后设置新密码

**工作台**：欢迎卡片 + 统计概览（员工数、分类数、饮品数，仅店长可见完整统计）

**员工管理**（仅店长）：表格 + 新增/编辑弹窗 + 启用/禁用开关 + 重置密码 + 删除确认

**分类管理**：表格 + 新增/编辑弹窗 + 删除检查关联

**饮品管理**：表格 + 按分类筛选 + 新增/编辑弹窗（含图片上传）+ 上架/下架开关

## 七、左侧菜单结构

```
仙贝奶茶店
├── 📊 工作台           ← 所有人
├── 👥 员工管理         ← 仅 MANAGER
├── 📂 分类管理         ← 所有人
└── 🧋 饮品管理         ← 所有人
```

## 八、非功能性需求

- 密码使用 BCrypt 加密存储
- JWT Token 过期时间 24 小时
- 前端路由守卫校验登录状态和角色权限
- 后端接口统一异常处理
