# 仙贝奶茶店前端视觉升级 · 设计文档

日期：2026-05-24
项目：ClaudeCodeProduct 前端视觉升级

---

## 1. 概述

为仙贝奶茶店管理系统前端添加品牌 Logo、登录页背景图、顾客端轮播图系统和员工页动画。

---

## 2. Logo 设计

**风格：** 茶杯图标 + 品牌字标

- 主体：精致茶杯剪影，内有茶汤涟漪
- 上方：两片茶叶 + 袅袅蒸汽曲线
- 品牌字：「仙贝奶茶店」金色 (#C8925C) 字标
- 格式：SVG 内联，无外部依赖
- 替换位置：
  - 登录页左侧品牌区（大尺寸 64px）
  - 侧边栏品牌区（小尺寸 28px）
  - 浏览器 tab 标题（文字）

---

## 3. 登录页背景

**风格：** 分层 SVG 装饰系统

- 底层：深蓝 (#1C2536) 到暖金 (#C8925C) 大渐变
- 中层：漂浮的茶叶剪影 + 茶杯轮廓水印装饰
- 上层：CSS 动画蒸汽粒子缓慢上浮
- 替换当前纯蓝色渐变 + 三个白色圆圈

---

## 4. 轮播图系统

### 4.1 后端

新建 `Banner` 实体，包含：
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| title | String | 标题 |
| imageUrl | String | 图片 URL（SVG 生成或外部图） |
| linkUrl | String | 点击跳转链接（可选） |
| sortOrder | Integer | 排序号 |
| enabled | Boolean | 是否启用 |
| createdAt | LocalDateTime | 创建时间 |

API 接口：
- `GET /api/public/banners` — 公开接口，获取启用的轮播图列表（按 sortOrder 排序）
- `POST /api/banners` — 新增轮播图 (MANAGER)
- `PUT /api/banners/{id}` — 编辑轮播图 (MANAGER)
- `DELETE /api/banners/{id}` — 删除轮播图 (MANAGER)

公开接口放到 `PublicController` 中，无需认证；管理接口放到独立 `BannerController`。

### 4.2 前端管理页

新增 `Banners.vue` 页面，店长可见，左侧菜单新增「轮播图管理」入口。

功能：
- 表格展示所有轮播图（标题、图片预览、排序、状态）
- 新增/编辑弹窗（标题、图片URL、链接、排序号）
- 删除确认
- 启用/禁用开关

### 4.3 前端顾客端展示

在 `Menu.vue` 顶部品牌栏下方插入轮播组件：
- 使用 Element Plus `<el-carousel>` 组件
- 自动播放，3秒间隔
- 指示器 dot 在底部
- 无图片时自动隐藏整个轮播区域
- API 调用：`GET /api/banners`（公开接口）

### 4.4 路由

- 管理后台：`/banners` → `Banners.vue`，`role: MANAGER`
- 公开接口路由由后端 `/api/banners` 提供

---

## 5. 员工页动画

在 `Users.vue` 添加精致专业风动画：

- **行交错滑入：** 每行依次淡入 + 从右向左滑入，间隔 60ms
- **按钮点击波纹：** 操作按钮点击时涟漪扩散效果
- **弹窗弹性入场：** el-dialog 打开时弹簧弹性效果（scale 从 0.9 弹到 1.0）
- **状态开关过渡：** el-switch 切换时卡片背景色平滑过渡
- **卡片微浮动：** el-card 悬停时微微上浮

---

## 6. 文件变更清单

### 新增文件
| 文件 | 说明 |
|------|------|
| frontend/src/views/Banners.vue | 轮播图管理页面 |
| backend/.../entity/Banner.java | Banner 实体 |
| backend/.../repository/BannerRepository.java | Banner 数据访问 |
| backend/.../controller/BannerController.java | Banner API 控制器 |

### 修改文件
| 文件 | 说明 |
|------|------|
| frontend/src/views/Login.vue | 替换 Logo SVG + 背景装饰 |
| frontend/src/layout/MainLayout.vue | 替换侧边栏 Logo SVG + 添加轮播图菜单 |
| frontend/src/views/Menu.vue | 添加轮播图组件到顶部 |
| frontend/src/views/Users.vue | 添加动画效果 |
| frontend/src/router/index.js | 添加轮播图管理路由 |
| frontend/src/api/index.js | 添加 bannerApi |
| frontend/src/assets/theme.css | 添加动画相关全局样式 |

---

## 7. 设计约束

- 所有图片均为 SVG 内联生成，零外部依赖
- 轮播图管理仅店长可见
- 轮播图顾客端展示无需登录
- 动画使用 CSS/JS，不额外引入动画库
- 与现有 Element Plus 主题完全兼容
