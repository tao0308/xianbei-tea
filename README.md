# 仙贝奶茶店管理系统

一个完整的奶茶店数字化运营平台，包含管理后台和顾客手机端。

## 项目结构

```
ClaudeCodeProduct/
├── backend/          # Spring Boot 3 + JPA + MySQL 后端
├── frontend/         # Vue 3 + Element Plus 后台管理
├── customer-app/     # Vue 3 + Vant UI 手机顾客端
└── docs/             # 设计文档 + 申请材料
```

## 功能模块

| 模块 | 说明 |
|------|------|
| 📊 工作台 | 销售趋势图、热销排行、库存预警 |
| 👥 员工管理 | 员工账号 CRUD、角色管控 |
| 🧋 饮品管理 | 饮品 CRUD、上下架、分类筛选 |
| 📋 订单管理 | 订单列表、状态流转、CSV 导出、语音播报 |
| 📦 库存管理 | 原料 CRUD、库存进度条、入库操作、低库存预警 |
| 🎯 套餐管理 | 自由搭配饮品+加料、自定义定价 |
| 🖼 轮播图管理 | 多图批量上传、排序、启用/禁用 |
| 📍 地址管理 | 顾客地址簿、默认地址、外卖选地址 |
| 🛵 外卖配送 | 5元配送费、地址簿选择、温度/甜度选项 |
| 🏪 到店自取 | 标准点单、加料选择、套餐购买 |

## 快速启动

### 后端
```bash
cd backend
mvn spring-boot:run
# 默认端口 8080，MySQL 数据库 xianbei_tea
```

### 管理后台
```bash
cd frontend
npm install
npm run dev
# 默认端口 3000
```

### 顾客手机端
```bash
cd customer-app
npm install
npm run dev
# 默认端口 3001
```

## 技术栈

- **后端:** Java 17, Spring Boot 3, Spring Security, JPA/Hibernate, MySQL, SSE
- **管理后台:** Vue 3, Element Plus, Pinia, ECharts
- **手机端:** Vue 3, Vant 4, Canvas (头像裁剪), Web Speech API
- **AI 工具:** Claude Agent (Cowork 模式), 多 Agent 并行开发, Skill 驱动工作流

## 开发方式

本项目完全通过 **Claude Agent + Cowork 模式** 开发，采用：
- `brainstorming` Skill → 需求分析与设计
- `writing-plans` Skill → 实施计划
- `subagent-driven-development` → 多子 Agent 并行
- `systematic-debugging` → 根因分析调试
- `ui-styling` → 设计系统精调

B站完整演示视频地址：https://www.bilibili.com/video/BV1dzGn6JEtV/?spm_id_from=333.1387.upload.video_card.click&vd_source=c86e016ce068b5cd234d2f1f79e88091
