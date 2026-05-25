# 仙贝奶茶店 · 订单系统设计文档

> 日期：2026-05-24
> 前置功能：账号管理、分类管理、饮品管理（已完成）

## 一、项目概述

为仙贝奶茶店后台管理系统新增订单模块。包括顾客端网页点单页、后台订单管理、加料管理。顾客无需登录即可点单，支持多杯下单、口味备注、加料选择。

## 二、数据表设计

### 2.1 orders（订单主表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 订单ID |
| order_no | VARCHAR(20) | NOT NULL, UNIQUE | 取餐号（如 A001） |
| customer_name | VARCHAR(50) | NOT NULL | 顾客姓名 |
| customer_phone | VARCHAR(20) | NOT NULL | 手机号 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | 已下单/制作中/已完成/已取餐 |
| total_price | DECIMAL(10,2) | NOT NULL | 总价 |
| remark | VARCHAR(500) | 可空 | 订单备注 |
| created_at | DATETIME | NOT NULL | 下单时间 |

状态枚举：`PENDING`（已下单）→ `MAKING`（制作中）→ `DONE`（已完成）→ `PICKED_UP`（已取餐）

### 2.2 order_item（订单明细）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| order_id | BIGINT | FK → orders.id | 关联订单 |
| product_id | BIGINT | FK → product.id | 饮品ID |
| product_name | VARCHAR(100) | NOT NULL | 冗余：下单时饮品名称（防止菜单变更影响历史订单） |
| quantity | INT | NOT NULL, DEFAULT 1 | 数量 |
| price | DECIMAL(10,2) | NOT NULL | 下单时单价 |
| taste_notes | VARCHAR(100) | 可空 | 口味备注（少糖、少冰、温等） |
| addons | VARCHAR(200) | 可空 | 加料说明（如：+珍珠+2元，+椰果+2元） |

### 2.3 addon（加料选项）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| name | VARCHAR(50) | NOT NULL | 加料名称（珍珠、椰果、布丁等） |
| price | DECIMAL(10,2) | NOT NULL, DEFAULT 0 | 加料价格 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 1启用 / 0禁用 |
| created_at | DATETIME | NOT NULL | 创建时间 |

## 三、API 接口

### 3.1 顾客端（无需登录，公开接口）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/public/categories | 分类列表（仅显示启用的分类） |
| GET | /api/public/products | 饮品列表（仅显示已上架饮品，支持按分类筛选） |
| GET | /api/public/addons | 可选的加料列表 |
| POST | /api/public/orders | 顾客下单 |

**POST /api/public/orders 请求示例：**
```json
{
  "customerName": "张三",
  "customerPhone": "13800138000",
  "remark": "",
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "tasteNotes": "少糖少冰",
      "addons": "珍珠+2元"
    },
    {
      "productId": 3,
      "quantity": 1,
      "tasteNotes": "去冰",
      "addons": ""
    }
  ]
}
```

### 3.2 后台订单管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/orders | 订单列表（支持 ?status= 筛选） |
| GET | /api/orders/{id} | 订单详情（含明细） |
| PUT | /api/orders/{id}/status | 更新订单状态 |

**PUT /api/orders/{id}/status 请求示例：**
```json
{ "status": "MAKING" }
```

### 3.3 后台加料管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/addons | 加料管理列表 |
| POST | /api/addons | 新增加料 |
| PUT | /api/addons/{id} | 编辑加料 |
| DELETE | /api/addons/{id} | 删除加料 |

## 四、前端页面

### 4.1 新增公开页面

**顾客点单页** (`/menu` — 无需登录，新开一个 Vue 项目或集成到现有前端）
- 顶部：店铺名「仙贝奶茶店」
- 分类 Tab 切换：招牌奶茶、鲜果茶等
- 饮品卡片网格：名称、价格
- 点击饮品 → 弹出点单窗口：数量选择、口味备注输入、加料勾选
- 底部购物车栏：点击展开侧栏，显示已选清单、填写姓名手机号、合计金额
- 提交订单 → 显示取餐号

### 4.2 新增后台页面

**订单管理** (`/orders`)
- 顶部状态筛选标签卡：全部 / 已下单 / 制作中 / 已完成 / 已取餐
- 卡片列表：取餐号（大号显示）、顾客名、手机号、饮品清单、下单时间
- 操作按钮：开始制作→制作完成→已取餐，按状态动态显示
- 订单详情弹窗：显示完整订单信息

**加料管理** (`/addons`)
- 简单表格：名称、价格、状态（启用/禁用开关）、操作
- 新增/编辑弹窗

### 4.3 左侧菜单更新

```
仙贝奶茶店
├── 📊 工作台
├── 👥 员工管理          ← 仅店长
├── 📋 订单管理          ← 新增
├── ➕ 加料管理          ← 新增
├── 📂 分类管理
└── 🧋 饮品管理
```

## 五、取餐号生成规则

- 按日期自增：当天第一单为 `A001`，第二单 `A002`，以此类推
- 每天零点重置

## 六、安全说明

- 顾客点单接口 `/api/public/**` 放行，不需要 JWT 校验
- 后台接口走现有的 JWT 认证
- 订单相关接口默认全员可用（店长和员工）
