# 套餐管理系统 · 设计文档

日期：2026-05-24
项目：ClaudeCodeProduct

---

## 1. 概述

为仙贝奶茶店后台管理系统添加套餐管理功能，支持店长自由搭配饮品和加料组成套餐、自定义定价、上传图片，顾客端可浏览和购买套餐。

---

## 2. 数据模型

### Combo（套餐表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 套餐名称（如"超值双人套餐"） |
| description | String/TEXT | 套餐描述 |
| price | BigDecimal | 套餐价（店长手动输入） |
| originalPrice | BigDecimal | 原价合计（自动计算，仅供参考） |
| imageUrl | String(500) | 套餐展示图片 |
| enabled | Boolean | 是否启用 |
| sortOrder | Integer | 排序号 |
| createdAt | LocalDateTime | 创建时间 |

### ComboItem（套餐明细表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| comboId | Long | 所属套餐 ID |
| productId | Long | 选中的饮品 ID |
| productName | String | 饮品名称（快照，菜单改变不影响历史） |
| quantity | Integer | 数量（如 2 杯） |
| addonIds | String/TEXT | 包含的加料 ID 列表，JSON 数组格式 |

---

## 3. API 设计

### 管理端 `/api/combos`（需 MANAGER 角色）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/combos | 获取全部套餐列表 |
| POST | /api/combos | 新增套餐（含明细） |
| PUT | /api/combos/{id} | 编辑套餐（含明细） |
| DELETE | /api/combos/{id} | 删除套餐 |

请求体格式（POST/PUT）：
```json
{
  "name": "超值双人套餐",
  "description": "两杯奶茶 + 一份加料",
  "price": 25.00,
  "imageUrl": "https://...",
  "enabled": true,
  "sortOrder": 0,
  "items": [
    { "productId": 1, "quantity": 2, "addonIds": "[1,2]" }
  ]
}
```

### 公开端 `/api/public/combos`（无需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/public/combos | 获取启用的套餐列表（含明细、产品名快照） |

---

## 4. 管理后台页面

### Combos.vue

- 左侧菜单新增「套餐管理」（店长可见）
- 页面布局同现有管理页面风格
- 表格列：图片、名称、价格、饮品数量、状态、操作
- 新增/编辑弹窗：
  - 基本信息区：名称、描述、图片URL、套餐价、排序、启用开关
  - 套餐内容区：
    - 从全部饮品列表中选择（下拉或勾选）
    - 每项可调整数量（input-number）
    - 每项可选包含的加料（checkbox 组）
    - 可添加多项饮品
    - 实时显示内容预览
  - 保存时提交套餐基本信息 + 明细列表

---

## 5. 顾客端展示

在 Menu.vue 分类 Tab 行末尾新增「套餐」Tab：
- 选中的套餐 Tab 时，商品网格显示套餐卡片
- 套餐卡片：图片（或占位 SVG） + 名称 + 价格 + "含 X 杯饮品"
- 点击套餐弹出点单弹窗：
  - 显示套餐名称、描述、价格
  - 显示套餐包含的饮品明细
  - 可选口味备注
  - 加入购物车，按套餐价结算

---

## 6. 文件变更清单

### 新增文件

| 文件 | 说明 |
|------|------|
| backend/.../entity/Combo.java | 套餐实体 |
| backend/.../entity/ComboItem.java | 套餐明细实体 |
| backend/.../repository/ComboRepository.java | 套餐数据访问 |
| backend/.../repository/ComboItemRepository.java | 明细数据访问 |
| backend/.../controller/ComboController.java | 套餐管理 API |
| frontend/src/views/Combos.vue | 套餐管理页面 |

### 修改文件

| 文件 | 说明 |
|------|------|
| backend/.../PublicController.java | 添加 /api/public/combos 接口 |
| frontend/src/api/index.js | 添加 comboApi + publicApi.combos |
| frontend/src/router/index.js | 添加 Combos 路由 |
| frontend/src/layout/MainLayout.vue | 添加侧边栏菜单项 |
| frontend/src/views/Menu.vue | 添加套餐 Tab 和展示 |

---

## 7. 设计约束

- 套餐内容使用产品名称快照（productName），菜单修改后套餐记录不受影响
- 套餐明细中的加料使用 JSON 字符串存储 ID 列表
- 套餐与原饮品平级展示，不互相影响
- 套餐加入购物车后按固定价结算，不随单品价格变动
