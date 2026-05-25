<img width="1920" height="920" alt="店长与员工登录页面" src="https://github.com/user-attachments/assets/d5c79910-7325-43c8-bf58-f98aafe5ef05" /># 仙贝奶茶店管理系统

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

用户端功能界面：
<img width="449" height="820" alt="注册页面" src="https://github.com/user-attachments/assets/16520ae4-f263-4d09-bf17-022cf24b0e80" />
<img width="460" height="820" alt="用户主页编辑" src="https://github.com/user-attachments/assets/54b55142-b960-419c-97b2-021a30593888" />
<img width="464" height="818" alt="选择奶茶页面" src="https://github.com/user-attachments/assets/74205e49-15a7-4393-9f2b-05144237c3ac" />
<img width="459" height="818" alt="我的订单页面" src="https://github.com/user-attachments/assets/7f5d16e6-ba58-45b8-8191-72fb34ff85cf" />
<img width="459" height="818" alt="手机端活动页面" src="https://github.com/user-attachments/assets/a68a2160-e0f0-4911-9730-cf0057b72ca6" />
<img width="459" height="818" alt="手机端点单页面" src="https://github.com/user-attachments/assets/a5d85c77-155b-49b5-aa2d-47390021f285" />
<img width="459" height="818" alt="评论页面" src="https://github.com/user-attachments/assets/9a2f9328-908a-41ed-9833-933e1ce3b6f3" />
<img width="459" height="818" alt="购物车页面" src="https://github.com/user-attachments/assets/77411794-345c-4d79-9d39-5a38f6421179" />
<img width="459" height="818" alt="点单成功页面" src="https://github.com/user-attachments/assets/92e8a586-b2be-41b5-a4fa-bcaf822c54cf" />
<img width="449" height="820" alt="登录页面" src="https://github.com/user-attachments/assets/eeacc569-8e60-497a-b7eb-2a9b6dc01b4b" />

员工与店长功能界面：
<img width="1920" height="920" alt="店长与员工登录页面" src="https://github.com/user-attachments/assets/cca6e359-e1fe-4ed1-8f63-88cf715a2db3" />
<img width="1920" height="920" alt="用户管理" src="https://github.com/user-attachments/assets/de391b4a-c072-4414-a5a0-1d9da1bf7887" />
<img width="1920" height="920" alt="员工管理" src="https://github.com/user-attachments/assets/6403aa58-1cb6-48ec-ba5f-94b7070b06c5" />
<img width="1920" height="920" alt="轮播图管理" src="https://github.com/user-attachments/assets/67ddb4cd-46ac-4e1a-af45-b0fb68812d34" />
<img width="1920" height="920" alt="套餐管理" src="https://github.com/user-attachments/assets/c1b90831-1192-4f3f-bf6a-10dc245eed29" />
<img width="1920" height="920" alt="饮品管理" src="https://github.com/user-attachments/assets/7e12b5f0-64f8-4137-a9d2-067e57cc6743" />
<img width="1920" height="920" alt="库存管理" src="https://github.com/user-attachments/assets/de1cb580-b355-4eab-82dc-a8b155573309" />
<img width="1920" height="920" alt="加料管理" src="https://github.com/user-attachments/assets/920911f0-b900-48aa-acc2-08036f660323" />
<img width="1920" height="920" alt="活动管理" src="https://github.com/user-attachments/assets/0233baaa-c6c0-421a-a43b-f035d61844ee" />
<img width="1920" height="920" alt="工作台" src="https://github.com/user-attachments/assets/4f3dae2d-22cb-4cde-a532-6ac786277e38" />
<img width="1920" height="920" alt="分类管理" src="https://github.com/user-attachments/assets/a56494cb-0b2b-4776-868b-b7d67a73d644" />
<img width="1920" height="920" alt="订单管理" src="https://github.com/user-attachments/assets/49fe67f1-ffa7-4392-88d3-d6077ff02b07" />





