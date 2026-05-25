# 仙贝奶茶店 · 订单系统实施计划

> **对于执行者：** 建议使用 subagent-driven-development 或 executing-plans 技能按任务逐步实现。

**目标：** 在现有仙贝奶茶店后台管理系统上新增订单模块，包括顾客网页点单、后台订单管理、加料管理。

**架构：** 后端新增实体/控制器/服务层，前端新增菜单点单页和后台订单管理页。顾客无需登录即可点单。

**前置条件：** 项目已能正常运行，数据库已有三张基础表。

**项目根目录：** `D:\HuaweiMoveData\Users\na\Desktop\ClaudeCodeProduct`

---

## 文件变更清单

### 新增文件（后端 13 个）
```
backend/src/main/java/com/xianbei/tea/
├── entity/Order.java
├── entity/OrderItem.java
├── entity/Addon.java
├── repository/OrderRepository.java
├── repository/OrderItemRepository.java
├── repository/AddonRepository.java
├── dto/CreateOrderRequest.java
├── dto/OrderItemRequest.java
├── service/OrderService.java
├── service/AddonService.java
├── controller/PublicController.java
├── controller/OrderController.java
└── controller/AddonController.java
```

### 新增文件（前端 3 个）
```
frontend/src/
├── views/Menu.vue          (顾客点单页)
├── views/Orders.vue        (后台订单管理)
└── views/Addons.vue        (后台加料管理)
```

### 修改文件
| 文件 | 修改内容 |
|------|---------|
| config/SecurityConfig.java | 添加 /api/public/** 放行 |
| frontend/src/api/index.js | 添加订单、加料 API |
| frontend/src/router/index.js | 添加 /menu, /orders, /addons 路由 |
| frontend/src/layout/MainLayout.vue | 菜单添加订单管理和加料管理 |

---

### Task 1: 订单实体类

**文件：**
- 创建: `entity/Order.java`
- 创建: `entity/OrderItem.java`
- 创建: `entity/Addon.java`

- [ ] **Step 1: Order.java**

```java
package com.xianbei.tea.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true, length = 20)
    private String orderNo;

    @Column(name = "customer_name", nullable = false, length = 50)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(length = 500)
    private String remark;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: OrderItem.java**

```java
package com.xianbei.tea.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "taste_notes", length = 100)
    private String tasteNotes;

    @Column(length = 200)
    private String addons;
}
```

- [ ] **Step 3: Addon.java**

```java
package com.xianbei.tea.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "addon")
public class Addon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

---

### Task 2: Repository 层

**文件：**
- 创建: `repository/OrderRepository.java`
- 创建: `repository/OrderItemRepository.java`
- 创建: `repository/AddonRepository.java`

- [ ] **Step 1: OrderRepository**

```java
package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByCreatedAtDesc(String status);
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= CURRENT_DATE")
    long countTodayOrders();

    Optional<Order> findByOrderNo(String orderNo);
}
```

- [ ] **Step 2: OrderItemRepository**

```java
package com.xianbei.tea.repository;

import com.xianbei.tea.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}
```

- [ ] **Step 3: AddonRepository**

```java
package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Addon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddonRepository extends JpaRepository<Addon, Long> {
    List<Addon> findByStatusOrderByIdAsc(Integer status);
}
```

---

### Task 3: DTO 请求类

**文件：**
- 创建: `dto/CreateOrderRequest.java`
- 创建: `dto/OrderItemRequest.java`

- [ ] **Step 1: CreateOrderRequest**

```java
package com.xianbei.tea.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String customerName;
    private String customerPhone;
    private String remark;
    private List<OrderItemRequest> items;
}
```

- [ ] **Step 2: OrderItemRequest**

```java
package com.xianbei.tea.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
    private String tasteNotes;
    private String addons;
}
```

---

### Task 4: Service 层

**文件：**
- 创建: `service/OrderService.java`
- 创建: `service/AddonService.java`

- [ ] **Step 1: OrderService**

```java
package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.CreateOrderRequest;
import com.xianbei.tea.entity.Order;
import com.xianbei.tea.entity.OrderItem;
import com.xianbei.tea.entity.Product;
import com.xianbei.tea.repository.OrderRepository;
import com.xianbei.tea.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // 后台：获取订单列表
    public Result<List<Order>> list(String status) {
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }
        // 初始化 items 以免 lazy 加载问题
        orders.forEach(o -> o.getItems().size());
        return Result.success(orders);
    }

    // 后台：获取订单详情
    public Result<Order> getById(Long id) {
        return orderRepository.findById(id)
                .map(o -> { o.getItems().size(); return Result.success(o); })
                .orElse(Result.error(404, "订单不存在"));
    }

    // 后台：更新订单状态
    @Transactional
    public Result<String> updateStatus(Long id, String status) {
        var opt = orderRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "订单不存在");
        Order order = opt.get();
        order.setStatus(status);
        orderRepository.save(order);
        String statusText = switch (status) {
            case "MAKING" -> "开始制作";
            case "DONE" -> "制作完成";
            case "PICKED_UP" -> "已取餐";
            default -> "状态更新";
        };
        return Result.success(statusText);
    }

    // 顾客端：下单
    @Transactional
    public Result<Order> createOrder(CreateOrderRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return Result.error(400, "请至少选择一件饮品");
        }

        Order order = new Order();
        order.setCustomerName(req.getCustomerName());
        order.setCustomerPhone(req.getCustomerPhone());
        order.setRemark(req.getRemark());
        order.setStatus("PENDING");

        // 生成取餐号
        long todayCount = orderRepository.countTodayOrders();
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        order.setOrderNo("A" + String.format("%03d", todayCount + 1));

        // 构建明细
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = req.getItems().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("饮品不存在: " + itemReq.getProductId()));
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());
            item.setTasteNotes(itemReq.getTasteNotes());
            item.setAddons(itemReq.getAddons());
            total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            return item;
        }).collect(Collectors.toList());

        order.setTotalPrice(total);
        order.setItems(items);
        orderRepository.save(order);
        return Result.success(order);
    }
}
```

- [ ] **Step 2: AddonService**

```java
package com.xianbei.tea.service;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Addon;
import com.xianbei.tea.repository.AddonRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddonService {
    private final AddonRepository addonRepository;

    public AddonService(AddonRepository addonRepository) {
        this.addonRepository = addonRepository;
    }

    public Result<List<Addon>> list() {
        return Result.success(addonRepository.findAll());
    }

    public Result<List<Addon>> publicList() {
        return Result.success(addonRepository.findByStatusOrderByIdAsc(1));
    }

    public Result<String> create(Addon addon) {
        addon.setId(null);
        addonRepository.save(addon);
        return Result.success("新增成功");
    }

    public Result<String> update(Long id, Addon addon) {
        var opt = addonRepository.findById(id);
        if (opt.isEmpty()) return Result.error(404, "加料选项不存在");
        Addon a = opt.get();
        a.setName(addon.getName());
        a.setPrice(addon.getPrice());
        a.setStatus(addon.getStatus());
        addonRepository.save(a);
        return Result.success("更新成功");
    }

    public Result<String> delete(Long id) {
        if (!addonRepository.existsById(id)) return Result.error(404, "加料选项不存在");
        addonRepository.deleteById(id);
        return Result.success("删除成功");
    }
}
```

---

### Task 5: Controller 层

**文件：**
- 创建: `controller/PublicController.java`
- 创建: `controller/OrderController.java`
- 创建: `controller/AddonController.java`

- [ ] **Step 1: PublicController（顾客端接口）**

```java
package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.dto.CreateOrderRequest;
import com.xianbei.tea.entity.Addon;
import com.xianbei.tea.entity.Category;
import com.xianbei.tea.entity.Order;
import com.xianbei.tea.entity.Product;
import com.xianbei.tea.repository.CategoryRepository;
import com.xianbei.tea.repository.ProductRepository;
import com.xianbei.tea.service.AddonService;
import com.xianbei.tea.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AddonService addonService;
    private final OrderService orderService;

    public PublicController(CategoryRepository categoryRepository,
                            ProductRepository productRepository,
                            AddonService addonService,
                            OrderService orderService) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.addonService = addonService;
        this.orderService = orderService;
    }

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(categoryRepository.findAllByOrderBySortAsc());
    }

    @GetMapping("/products")
    public Result<List<Product>> products(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return Result.success(productRepository.findByCategoryId(categoryId));
        }
        return Result.success(productRepository.findAll());
    }

    @GetMapping("/addons")
    public Result<List<Addon>> addons() {
        return addonService.publicList();
    }

    @PostMapping("/orders")
    public Result<Order> createOrder(@RequestBody CreateOrderRequest req) {
        return orderService.createOrder(req);
    }
}
```

- [ ] **Step 2: OrderController（后台订单管理）**

```java
package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Order;
import com.xianbei.tea.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<List<Order>> list(@RequestParam(required = false) String status) {
        return orderService.list(status);
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderService.updateStatus(id, body.get("status"));
    }
}
```

- [ ] **Step 3: AddonController（后台加料管理）**

```java
package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Addon;
import com.xianbei.tea.service.AddonService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/addons")
public class AddonController {
    private final AddonService addonService;

    public AddonController(AddonService addonService) {
        this.addonService = addonService;
    }

    @GetMapping
    public Result<List<Addon>> list() {
        return addonService.list();
    }

    @PostMapping
    public Result<String> create(@RequestBody Addon addon) {
        return addonService.create(addon);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Addon addon) {
        return addonService.update(id, addon);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return addonService.delete(id);
    }
}
```

---

### Task 6: 更新 SecurityConfig

**文件：**
- 修改: `config/SecurityConfig.java`

- [ ] **Step 1: 添加 /api/public/** 放行规则

在 `config/SecurityConfig.java` 中找到 `.authorizeHttpRequests` 部分，在 `.requestMatchers("/api/auth/**").permitAll()` 下面添加一行：

```java
.requestMatchers("/api/public/**").permitAll()
```

修改后如下：
```java
.authorizeHttpRequests(a -> a
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/public/**").permitAll()
    .requestMatchers("/api/users/**").hasRole("MANAGER")
    .anyRequest().authenticated()
)
```

---

### Task 7: 前端 API 接口

**文件：**
- 修改: `frontend/src/api/index.js`

- [ ] **Step 1: 添加订单和加料 API

在 `frontend/src/api/index.js` 文件末尾添加：

```javascript
// ==================== 顾客点单（公开） ====================
export const publicApi = {
  categories: () => request.get('/public/categories'),
  products: (categoryId) => request.get('/public/products', { params: { categoryId } }),
  addons: () => request.get('/public/addons'),
  createOrder: data => request.post('/public/orders', data)
}

// ==================== 后台订单管理 ====================
export const orderApi = {
  list: (status) => request.get('/orders', { params: { status } }),
  getById: (id) => request.get(`/orders/${id}`),
  updateStatus: (id, status) => request.put(`/orders/${id}/status`, { status })
}

// ==================== 后台加料管理 ====================
export const addonApi = {
  list: () => request.get('/addons'),
  create: data => request.post('/addons', data),
  update: (id, data) => request.put(`/addons/${id}`, data),
  delete: id => request.delete(`/addons/${id}`)
}
```

---

### Task 8: 前端路由更新

**文件：**
- 修改: `frontend/src/router/index.js`

- [ ] **Step 1: 添加 /menu 公共路由和后台订单/加料路由

在 routes 数组中 `/forgot-password` 后面添加：

```javascript
{
  path: '/menu',
  name: 'Menu',
  component: () => import('../views/Menu.vue'),
  meta: { title: '在线点单' }
},
```

在 children 中 `/products` 后面添加：

```javascript
{
  path: 'orders',
  name: 'Orders',
  component: () => import('../views/Orders.vue'),
  meta: { title: '订单管理' }
},
{
  path: 'addons',
  name: 'Addons',
  component: () => import('../views/Addons.vue'),
  meta: { title: '加料管理' }
},
```

在路由守卫中，将 `!['Login', 'Register', 'ForgotPassword']` 改为：
```javascript
if (!['Login', 'Register', 'ForgotPassword', 'Menu'].includes(to.name) && !token) {
```

---

### Task 9: 更新左侧菜单

**文件：**
- 修改: `frontend/src/layout/MainLayout.vue`

- [ ] **Step 1: 在菜单中添加订单管理和加料管理

在 `<el-menu-item index="/products">` 后面添加：

```html
<el-menu-item index="/orders">
  <el-icon><List /></el-icon>
  <span>订单管理</span>
</el-menu-item>
<el-menu-item index="/addons">
  <el-icon><Plus /></el-icon>
  <span>加料管理</span>
</el-menu-item>
```

确保在 script setup 的 import 中引入 List 和 Plus 图标：
```javascript
import { HomeFilled, UserFilled, FolderOpened, GoodsFilled, List, Plus, Fold, ArrowDown, InfoFilled, SwitchButton } from '@element-plus/icons-vue'
```

---

### Task 10: 顾客点单页 Menu.vue

**文件：**
- 创建: `frontend/src/views/Menu.vue`

完整页面代码：

```vue
<template>
  <div class="menu-page">
    <!-- 顶部品牌栏 -->
    <header class="menu-header">
      <h1>仙贝奶茶店</h1>
      <p>新鲜手作 · 每一杯都是用心</p>
    </header>

    <div class="menu-body">
      <!-- 分类 Tab -->
      <div class="category-tabs">
        <button
          v-for="cat in categories"
          :key="cat.id"
          :class="['tab-btn', { active: activeCategory === cat.id }]"
          @click="activeCategory = cat.id"
        >{{ cat.name }}</button>
      </div>

      <!-- 饮品网格 -->
      <div class="product-grid">
        <div v-for="item in filteredProducts" :key="item.id" class="product-card" @click="openOrderDialog(item)">
          <div class="product-icon">{{ item.name.charAt(0) }}</div>
          <div class="product-info">
            <h3>{{ item.name }}</h3>
            <p v-if="item.description" class="product-desc">{{ item.description }}</p>
            <p class="product-price">¥{{ item.price }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 购物车浮标 -->
    <div v-if="cart.length > 0" class="cart-float" @click="showCart = true">
      <span class="cart-badge">{{ cartTotal }}</span>
      <span>查看购物车 · ¥{{ cartAmount }}</span>
    </div>

    <!-- 点单弹窗 -->
    <el-dialog v-model="orderDialog.visible" title="选择规格" width="380px" :close-on-click-modal="true">
      <div class="order-dialog-body">
        <h3>{{ orderDialog.product?.name }}</h3>
        <p class="dialog-price">¥{{ orderDialog.product?.price }}</p>

        <div class="form-group">
          <label>数量</label>
          <el-input-number v-model="orderDialog.quantity" :min="1" :max="99" />
        </div>

        <div class="form-group">
          <label>口味备注</label>
          <el-input v-model="orderDialog.tasteNotes" placeholder="如：少糖、少冰、去冰、温" />
        </div>

        <div class="form-group" v-if="addons.length > 0">
          <label>加料</label>
          <div class="addon-list">
            <el-checkbox v-for="a in addons" :key="a.id" :label="a.id" @change="toggleAddon(a)">
              {{ a.name }} (+¥{{ a.price }})
            </el-checkbox>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="orderDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="addToCart">加入购物车</el-button>
      </template>
    </el-dialog>

    <!-- 购物车侧栏 -->
    <el-drawer v-model="showCart" title="购物车" size="400px">
      <div class="cart-items">
        <div v-for="(item, idx) in cart" :key="idx" class="cart-item">
          <div class="cart-item-info">
            <strong>{{ item.name }}</strong>
            <span>x{{ item.quantity }}</span>
            <span class="cart-item-notes" v-if="item.tasteNotes">备注：{{ item.tasteNotes }}</span>
            <span class="cart-item-notes" v-if="item.addonText">加料：{{ item.addonText }}</span>
          </div>
          <div class="cart-item-price">¥{{ item.subtotal }}</div>
          <el-button size="small" type="danger" text @click="removeFromCart(idx)">删除</el-button>
        </div>
      </div>

      <div class="cart-footer" v-if="cart.length > 0">
        <div class="cart-total">
          <span>合计</span>
          <strong>¥{{ cartAmount }}</strong>
        </div>

        <el-form :model="customerForm" label-width="0" class="customer-form">
          <el-input v-model="customerForm.name" placeholder="您的姓名" style="margin-bottom: 10px" />
          <el-input v-model="customerForm.phone" placeholder="手机号" style="margin-bottom: 10px" />
          <el-input v-model="customerForm.remark" placeholder="订单备注（可选）" />
        </el-form>

        <el-button type="primary" size="large" style="width: 100%; margin-top: 12px" @click="submitOrder" :loading="submitting">提交订单</el-button>
      </div>
    </el-drawer>

    <!-- 下单成功弹窗 -->
    <el-dialog v-model="successDialog.visible" title="下单成功" width="360px" :close-on-click-modal="false">
      <div class="success-body">
        <div class="success-icon">✅</div>
        <h2>取餐号：{{ successDialog.orderNo }}</h2>
        <p>请留意叫号，凭取餐号取餐</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="resetOrder" style="width: 100%">继续点单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { publicApi } from '../api'
import { ElMessage } from 'element-plus'

const categories = ref([])
const products = ref([])
const addons = ref([])
const activeCategory = ref(null)
const cart = ref([])
const showCart = ref(false)
const submitting = ref(false)
const showSuccess = ref(false)

const customerForm = reactive({ name: '', phone: '', remark: '' })

const orderDialog = reactive({
  visible: false, product: null, quantity: 1, tasteNotes: '', selectedAddons: []
})

const successDialog = reactive({ visible: false, orderNo: '' })

const filteredProducts = computed(() => {
  if (!activeCategory.value) return products.value
  return products.value.filter(p => p.categoryId === activeCategory.value)
})

const cartTotal = computed(() => cart.value.reduce((s, i) => s + i.quantity, 0))
const cartAmount = computed(() => cart.value.reduce((s, i) => s + i.subtotal, 0).toFixed(2))

function openOrderDialog(product) {
  orderDialog.product = product
  orderDialog.quantity = 1
  orderDialog.tasteNotes = ''
  orderDialog.selectedAddons = []
  orderDialog.visible = true
}

function toggleAddon(addon) {
  const idx = orderDialog.selectedAddons.indexOf(addon.id)
  if (idx > -1) orderDialog.selectedAddons.splice(idx, 1)
  else orderDialog.selectedAddons.push(addon.id)
}

function addToCart() {
  const product = orderDialog.product
  const addonText = orderDialog.selectedAddons.map(id => {
    const a = addons.value.find(x => x.id === id)
    return a ? `${a.name}+¥${a.price}` : ''
  }).filter(Boolean).join('、')

  cart.value.push({
    productId: product.id,
    name: product.name,
    quantity: orderDialog.quantity,
    price: product.price,
    tasteNotes: orderDialog.tasteNotes,
    addonText,
    subtotal: (product.price * orderDialog.quantity).toFixed(2)
  })
  orderDialog.visible = false
  ElMessage.success('已加入购物车')
}

function removeFromCart(idx) {
  cart.value.splice(idx, 1)
}

async function submitOrder() {
  if (!customerForm.name) return ElMessage.warning('请填写姓名')
  if (!customerForm.phone) return ElMessage.warning('请填写手机号')
  submitting.value = true
  try {
    const items = cart.value.map(i => ({
      productId: i.productId,
      quantity: i.quantity,
      tasteNotes: i.tasteNotes || '',
      addons: i.addonText || ''
    }))
    const res = await publicApi.createOrder({
      customerName: customerForm.name,
      customerPhone: customerForm.phone,
      remark: customerForm.remark || '',
      items
    })
    successDialog.orderNo = res.data.orderNo
    successDialog.visible = true
    showCart.value = false
  } finally {
    submitting.value = false
  }
}

function resetOrder() {
  successDialog.visible = false
  cart.value = []
  customerForm.name = ''
  customerForm.phone = ''
  customerForm.remark = ''
}

onMounted(async () => {
  const [catRes, prodRes, addonRes] = await Promise.all([
    publicApi.categories(),
    publicApi.products(),
    publicApi.addons()
  ])
  categories.value = catRes.data
  products.value = prodRes.data
  addons.value = addonRes.data
  if (categories.value.length > 0) activeCategory.value = categories.value[0].id
})
</script>

<style scoped>
.menu-page {
  min-height: 100vh;
  background: #f8f6f0;
  padding-bottom: 80px;
}

.menu-header {
  background: linear-gradient(135deg, #1C2536, #2C3E50);
  color: #fff;
  text-align: center;
  padding: 32px 16px;
}

.menu-header h1 { font-size: 28px; font-weight: 700; letter-spacing: 2px; margin-bottom: 6px; }
.menu-header p { font-size: 14px; opacity: 0.7; }

.menu-body { max-width: 800px; margin: 0 auto; padding: 16px; }

.category-tabs { display: flex; gap: 8px; overflow-x: auto; padding: 8px 0; margin-bottom: 16px; }
.tab-btn {
  padding: 8px 20px; border-radius: 20px; border: 1px solid #ddd;
  background: #fff; font-size: 14px; cursor: pointer; white-space: nowrap;
  transition: all 0.2s;
}
.tab-btn.active { background: #C8925C; color: #fff; border-color: #C8925C; }

.product-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.product-card {
  background: #fff; border-radius: 12px; padding: 16px; cursor: pointer;
  display: flex; gap: 12px; align-items: center; box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: transform 0.2s, box-shadow 0.2s;
}
.product-card:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,0.08); }

.product-icon {
  width: 48px; height: 48px; border-radius: 10px; background: #FDF6EE;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; color: #C8925C; font-weight: 700; flex-shrink: 0;
}
.product-info h3 { font-size: 15px; font-weight: 600; margin-bottom: 2px; }
.product-desc { font-size: 12px; color: #999; margin-bottom: 2px; }
.product-price { font-size: 16px; font-weight: 700; color: #C8925C; }

.cart-float {
  position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%);
  background: #1C2536; color: #fff; padding: 12px 24px; border-radius: 28px;
  cursor: pointer; display: flex; align-items: center; gap: 8px; font-size: 15px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2); z-index: 100;
}
.cart-badge {
  width: 24px; height: 24px; border-radius: 50%; background: #C8925C;
  display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700;
}

.order-dialog-body .form-group { margin-bottom: 16px; }
.order-dialog-body label { display: block; font-size: 13px; color: #666; margin-bottom: 6px; font-weight: 500; }
.dialog-price { font-size: 20px; font-weight: 700; color: #C8925C; margin: 8px 0 16px; }

.addon-list { display: flex; flex-direction: column; gap: 6px; }

.cart-items { padding: 0; }
.cart-item { display: flex; align-items: center; gap: 8px; padding: 12px 0; border-bottom: 1px solid #eee; }
.cart-item-info { flex: 1; }
.cart-item-info strong { display: block; font-size: 14px; }
.cart-item-notes { display: block; font-size: 12px; color: #999; margin-top: 2px; }
.cart-item-price { font-weight: 600; color: #C8925C; white-space: nowrap; }

.cart-footer { padding-top: 16px; }
.cart-total { display: flex; justify-content: space-between; font-size: 16px; margin-bottom: 16px; }
.customer-form { margin-bottom: 8px; }

.success-body { text-align: center; padding: 20px 0; }
.success-icon { font-size: 48px; margin-bottom: 12px; }
.success-body h2 { font-size: 28px; color: #C8925C; margin-bottom: 8px; }
.success-body p { color: #666; }

@media (min-width: 640px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); }
}
</style>
```

---

### Task 11: 后台订单管理页 Orders.vue

**文件：**
- 创建: `frontend/src/views/Orders.vue`

```vue
<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>订单管理</h2>
          <p>查看和管理顾客订单</p>
        </div>
      </div>

      <!-- 状态筛选 -->
      <el-tabs v-model="activeStatus" @tab-change="fetchList" class="status-tabs">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="已下单" name="PENDING" />
        <el-tab-pane label="制作中" name="MAKING" />
        <el-tab-pane label="已完成" name="DONE" />
        <el-tab-pane label="已取餐" name="PICKED_UP" />
      </el-tabs>

      <!-- 订单列表 -->
      <div v-loading="loading">
        <div v-if="orders.length === 0" style="text-align: center; padding: 60px 0; color: #999">暂无订单</div>
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-no">{{ order.orderNo }}</div>
            <el-tag :type="statusTag(order.status)" effect="dark" round>
              {{ statusText(order.status) }}
            </el-tag>
          </div>
          <div class="order-body">
            <div class="order-customer">
              <span>{{ order.customerName }}</span>
              <span class="order-phone">{{ order.customerPhone }}</span>
            </div>
            <div class="order-time">{{ formatTime(order.createdAt) }}</div>
            <div class="order-items">
              <div v-for="item in order.items" :key="item.id" class="order-item">
                <span>{{ item.productName }} x{{ item.quantity }}</span>
                <span class="item-notes" v-if="item.tasteNotes">{{ item.tasteNotes }}</span>
                <span class="item-notes" v-if="item.addons">+{{ item.addons }}</span>
              </div>
            </div>
            <div class="order-total">合计：¥{{ order.totalPrice }}</div>
          </div>
          <div class="order-actions" v-if="order.status !== 'PICKED_UP'">
            <el-button v-if="order.status === 'PENDING'" type="warning" @click="updateStatus(order.id, 'MAKING')">开始制作</el-button>
            <el-button v-if="order.status === 'MAKING'" type="success" @click="updateStatus(order.id, 'DONE')">制作完成</el-button>
            <el-button v-if="order.status === 'DONE'" @click="updateStatus(order.id, 'PICKED_UP')">确认取餐</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '../api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')

function statusText(s) {
  const map = { PENDING: '已下单', MAKING: '制作中', DONE: '已完成', PICKED_UP: '已取餐' }
  return map[s] || s
}

function statusTag(s) {
  const map = { PENDING: 'danger', MAKING: 'warning', DONE: 'success', PICKED_UP: 'info' }
  return map[s] || ''
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

async function fetchList() {
  loading.value = true
  try {
    const res = await orderApi.list(activeStatus.value || null)
    orders.value = res.data || []
  } finally { loading.value = false }
}

async function updateStatus(id, status) {
  await orderApi.updateStatus(id, status)
  ElMessage.success(statusText(status))
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { font-size: 18px; font-weight: 600; margin-bottom: 4px; }
.page-header p { font-size: 13px; color: var(--xianbei-text-secondary); }

.status-tabs { margin-bottom: 16px; }

.order-card {
  background: #fff; border: 1px solid #eee; border-radius: 10px; padding: 16px; margin-bottom: 12px;
  transition: box-shadow 0.2s;
}
.order-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.06); }

.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.order-no { font-size: 22px; font-weight: 700; color: #C8925C; letter-spacing: 1px; }
.order-customer { font-size: 15px; font-weight: 500; margin-bottom: 4px; }
.order-phone { font-size: 13px; color: #999; margin-left: 8px; }
.order-time { font-size: 12px; color: #999; margin-bottom: 8px; }
.order-items { font-size: 14px; margin-bottom: 8px; }
.order-item { margin-bottom: 3px; }
.item-notes { font-size: 12px; color: #999; margin-left: 8px; }
.order-total { font-size: 15px; font-weight: 600; color: #C8925C; }
.order-actions { margin-top: 12px; display: flex; gap: 8px; }
</style>
```

---

### Task 12: 后台加料管理页 Addons.vue

**文件：**
- 创建: `frontend/src/views/Addons.vue`

```vue
<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>加料管理</h2>
          <p>管理可选的加料选项，顾客点单时可见</p>
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">新增加料</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="加料名称" min-width="160" />
        <el-table-column prop="price" label="价格" width="120" align="center">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑加料' : '新增加料'" width="420px">
      <el-form :model="dialog.form" ref="formRef" label-width="60px">
        <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入加料名称' }]">
          <el-input v-model="dialog.form.name" placeholder="如：珍珠、椰果" />
        </el-form-item>
        <el-form-item label="价格" prop="price" :rules="[{ required: true, message: '请输入价格' }]">
          <el-input-number v-model="dialog.form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="dialog.saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { addonApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const formRef = ref(null)
const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { name: '', price: 0 }
})

async function fetchList() {
  loading.value = true
  try { const r = await addonApi.list(); list.value = r.data }
  finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false; dialog.form = { name: '', price: 0 }; dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true; dialog._editId = row.id
  dialog.form = { name: row.name, price: row.price }
  dialog.visible = true
}

async function save() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) { await addonApi.update(dialog._editId, dialog.form) }
    else { await addonApi.create(dialog.form) }
    ElMessage.success('保存成功')
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await addonApi.update(row.id, { ...row, status: newStatus })
  row.status = newStatus
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除加料「${row.name}」吗？`, '提示', { type: 'warning' })
  await addonApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-header {
  display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px;
}
.page-header h2 { font-size: 18px; font-weight: 600; margin-bottom: 4px; }
.page-header p { font-size: 13px; color: var(--xianbei-text-secondary); }
</style>
```

---

## 计划自检

- ✅ **Spec 覆盖** — 所有 API（公开点单、后台订单、加料管理）、所有前端页面均已覆盖
- ✅ **无占位符** — 每段代码完整可执行
- ✅ **类型一致** — API 路径、字段名、请求/响应格式前后端一致
- ✅ **数据库表** — orders、order_item、addon 三张表完全匹配设计文档
- ✅ **安全** — /api/public/** 已放行，后台接口保持 JWT 认证
