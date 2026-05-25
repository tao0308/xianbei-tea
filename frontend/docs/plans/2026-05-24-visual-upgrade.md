# 前端视觉升级 + 轮播图系统 Implementation Plan

**Goal:** 为仙贝奶茶店添加品牌 Logo SVG、精美登录页背景、顾客端轮播图系统、员工页动画
**Architecture:** 纯前端视觉升级（Logo/背景/动画）+ 新后端 Banner 模块（实体 → 仓库 → 管理 API → 公开 API → 前端管理页 → 顾客端展示）
**Tech Stack:** Vue 3 + Element Plus, Spring Boot 3 + JPA + MySQL, SVG/CSS Animations

---

## Task 1 — 替换 Logo SVG（Login.vue + MainLayout.vue）

**文件修改：**
- `frontend/src/views/Login.vue`
- `frontend/src/layout/MainLayout.vue`
- `frontend/src/assets/theme.css`

### 步骤 1.1 — 生成精致茶杯 Logo SVG

创建一个可复用的 LOGO_SVG 常量，茶杯 + 茶叶 + 蒸汽：

```svg
<svg width="..." height="..." viewBox="0 0 64 64" fill="none">
  <!-- 茶杯主体 -->
  <path d="M16 26C16 22 20 18 32 18C44 18 48 22 48 26V42C48 46 44 50 32 50C20 50 16 46 16 42V26Z" 
        fill="url(#cupGrad)" stroke="#C8925C" stroke-width="1.5"/>
  <!-- 杯把 -->
  <path d="M48 28C54 28 56 32 54 36C52 40 48 40 48 38" 
        stroke="#C8925C" stroke-width="1.8" fill="none" stroke-linecap="round"/>
  <!-- 茶汤 -->
  <ellipse cx="32" cy="26" rx="16" ry="4" fill="#DFB484" opacity="0.6"/>
  <!-- 茶叶1 -->
  <path d="M24 22C22 18 26 14 30 16" stroke="#5B8C5A" stroke-width="1.5" stroke-linecap="round" fill="none"/>
  <path d="M30 16C32 14 36 16 34 18" stroke="#5B8C5A" stroke-width="1.5" stroke-linecap="round" fill="none"/>
  <!-- 蒸汽 -->
  <path d="M28 14C27 10 29 8 28 6" stroke="#C8925C" stroke-width="1" stroke-linecap="round" opacity="0.4" fill="none"/>
  <path d="M34 14C35 11 33 9 34 7" stroke="#C8925C" stroke-width="1" stroke-linecap="round" opacity="0.3" fill="none"/>
  <defs>
    <linearGradient id="cupGrad" x1="32" y1="18" x2="32" y2="50">
      <stop offset="0%" stop-color="#FFF8F0"/>
      <stop offset="100%" stop-color="#FDF6EE"/>
    </linearGradient>
  </defs>
</svg>
```

**具体操作：**

1. 打开 `Login.vue`，把第 15-21 行的旧 SVG 替换为上面的新 SVG（调整尺寸为 72x72）
2. 打开 `MainLayout.vue`，把第 8-14 行的旧侧边栏 SVG 替换为同一 SVG（调整尺寸为 28x28）

### 步骤 1.2 — 更新品牌字样式

在 `Login.vue` 中，品牌标题 `.brand-title` 保持 "仙贝奶茶店"，增加一个金色下划线装饰。

### 步骤 1.3 — 侧边栏品牌区加字标

在 `MainLayout.vue` 中，`<span class="brand-text">` 已经是 "仙贝奶茶店"，无需改动。

---

## Task 2 — 替换登录页背景装饰（Login.vue）

**文件修改：**
- `frontend/src/views/Login.vue`

### 步骤 2.1 — 用 SVG 装饰替换纯色圆圈

把登录页 `.login-bg` 中的 3 个白色圆圈替换为茶主题 SVG 装饰元素：

1. **背景渐变** — 保持 `linear-gradient(135deg, #667eea 0%, #764ba2 100%)` 不变（效果好）
2. **装饰 SVG** — 在底层添加漂浮的茶叶和茶杯剪影：

```html
<!-- 替换 login-bg 内部 -->
<div class="login-bg">
  <!-- 保留渐变背景 -->
  <div class="bg-overlay"></div>
  <!-- 装饰元素 -->
  <div class="deco-leaf deco-leaf-1">
    <svg width="120" height="120" viewBox="0 0 120 120">
      <path d="M60 100C40 80 30 50 50 30C60 20 70 25 70 25C50 40 55 70 70 85Z" 
            fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.12)" stroke-width="1"/>
    </svg>
  </div>
  <div class="deco-leaf deco-leaf-2">
    <svg width="80" height="100" viewBox="0 0 80 100">
      <path d="M40 80C20 60 10 35 30 20C40 12 55 18 60 22C35 35 40 65 55 78Z" 
            fill="rgba(255,255,255,0.06)" stroke="rgba(255,255,255,0.1)" stroke-width="1"/>
    </svg>
  </div>
  <!-- 蒸汽粒子动画 -->
  <div class="steam-particle" v-for="n in 6" :key="n" :style="{ left: (15 + n * 14) + '%', animationDelay: (n * 0.8) + 's' }">
    <svg width="24" height="40" viewBox="0 0 24 40">
      <path d="M12 40C10 30 14 24 12 16" stroke="rgba(255,255,255,0.15)" stroke-width="1.5" stroke-linecap="round" fill="none"/>
    </svg>
  </div>
</div>
```

### 步骤 2.2 — 添加蒸汽动画 CSS

```css
.deco-leaf-1 { position: absolute; top: 10%; left: 5%; opacity: 0.5; animation: floatLeaf 8s ease-in-out infinite; }
.deco-leaf-2 { position: absolute; bottom: 15%; right: 8%; opacity: 0.4; animation: floatLeaf 10s ease-in-out infinite reverse; }
.steam-particle { position: absolute; bottom: 45%; animation: steamRise 4s ease-out infinite; }
@keyframes floatLeaf { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-20px) rotate(5deg); } }
@keyframes steamRise { 0% { opacity: 0; transform: translateY(0) scale(1); } 30% { opacity: 0.6; } 100% { opacity: 0; transform: translateY(-80px) scale(1.5); } }
```

---

## Task 3 — 员工页动画（Users.vue）

**文件修改：**
- `frontend/src/views/Users.vue`
- `frontend/src/assets/theme.css`

### 步骤 3.1 — 行交错滑入动画

在 `Users.vue` `<style scoped>` 中添加：

```css
/* 交错行动画 */
.el-table__body-wrapper tbody tr {
  animation: rowSlideIn 0.4s ease backwards;
}
.el-table__body-wrapper tbody tr:nth-child(1) { animation-delay: 0ms; }
.el-table__body-wrapper tbody tr:nth-child(2) { animation-delay: 60ms; }
.el-table__body-wrapper tbody tr:nth-child(3) { animation-delay: 120ms; }
.el-table__body-wrapper tbody tr:nth-child(4) { animation-delay: 180ms; }
.el-table__body-wrapper tbody tr:nth-child(5) { animation-delay: 240ms; }
.el-table__body-wrapper tbody tr:nth-child(6) { animation-delay: 300ms; }

@keyframes rowSlideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}
```

### 步骤 3.2 — 按钮点击波纹

在 `Users.vue` `<style scoped>` 中添加：

```css
.el-button {
  position: relative;
  overflow: hidden;
}
.el-button::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: radial-gradient(circle at var(--ripple-x, 50%) var(--ripple-y, 50%), rgba(255,255,255,0.4) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}
.el-button:active::after {
  opacity: 1;
  transition: opacity 0s;
}
```

或者在 `<script setup>` 中添加点击波纹 JS（通过监听所有按钮的点击事件设置 --ripple-x/y）。

简化方式：用 CSS 伪类 `::after` + `active` 触发即可，不额外增加 JS。

### 步骤 3.3 — 弹窗弹性入场

在 `Users.vue` `<style scoped>` 中添加：

```css
:deep(.el-dialog) {
  animation: dialogBounce 0.3s ease;
}
@keyframes dialogBounce {
  0% { transform: scale(0.9); opacity: 0; }
  60% { transform: scale(1.02); }
  100% { transform: scale(1); opacity: 1; }
}
```

### 步骤 3.4 — 状态开关过渡

el-switch 默认已有平滑过渡，无需额外 CSS。但为卡片列表添加悬停微浮动效果：

```css
.el-card :deep(.page-card) {
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.el-card :deep(.page-card):hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.08);
}
```

---

## Task 4 — 后端 Banner 模块

### 步骤 4.1 — 创建 Banner.java 实体

**新建文件：** `backend/src/main/java/com/xianbei/tea/entity/Banner.java`

```java
package com.xianbei.tea.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "banner")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
```

### 步骤 4.2 — 创建 BannerRepository.java

**新建文件：** `backend/src/main/java/com/xianbei/tea/repository/BannerRepository.java`

```java
package com.xianbei.tea.repository;

import com.xianbei.tea.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByEnabledTrueOrderBySortOrderAsc();
    List<Banner> findAllByOrderBySortOrderAsc();
}
```

### 步骤 4.3 — 创建 BannerController.java（管理端）

**新建文件：** `backend/src/main/java/com/xianbei/tea/controller/BannerController.java`

```java
package com.xianbei.tea.controller;

import com.xianbei.tea.common.Result;
import com.xianbei.tea.entity.Banner;
import com.xianbei.tea.repository.BannerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/banners")
public class BannerController {
    private final BannerRepository bannerRepository;

    public BannerController(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @GetMapping
    public Result<List<Banner>> list() {
        return Result.success(bannerRepository.findAllByOrderBySortOrderAsc());
    }

    @PostMapping
    public Result<Banner> create(@RequestBody Banner banner) {
        return Result.success(bannerRepository.save(banner));
    }

    @PutMapping("/{id}")
    public Result<Banner> update(@PathVariable Long id, @RequestBody Banner banner) {
        Banner existing = bannerRepository.findById(id).orElse(null);
        if (existing == null) return Result.error(404, "轮播图不存在");
        existing.setTitle(banner.getTitle());
        existing.setImageUrl(banner.getImageUrl());
        existing.setLinkUrl(banner.getLinkUrl());
        existing.setSortOrder(banner.getSortOrder());
        existing.setEnabled(banner.getEnabled());
        return Result.success(bannerRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerRepository.deleteById(id);
        return Result.success(null);
    }
}
```

### 步骤 4.4 — 添加公开 Banner API 到 PublicController.java

在 `PublicController.java` 中添加：

```java
import com.xianbei.tea.entity.Banner;
import com.xianbei.tea.repository.BannerRepository;

// 在类中注入
private final BannerRepository bannerRepository;

// 在构造方法参数中增加 BannerRepository bannerRepository
// this.bannerRepository = bannerRepository;

@GetMapping("/banners")
public Result<List<Banner>> banners() {
    return Result.success(bannerRepository.findByEnabledTrueOrderBySortOrderAsc());
}
```

---

## Task 5 — 前端轮播图管理页（Banners.vue）

**新建文件：** `frontend/src/views/Banners.vue`

```vue
<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>轮播图管理</h2>
          <p>管理顾客点单页顶部轮播图片，仅店长可见</p>
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">新增轮播图</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading" style="width: 100%">
        <el-table-column label="图片" width="120" align="center">
          <template #default="{ row }">
            <img v-if="row.imageUrl" :src="row.imageUrl" style="width:80px;height:45px;object-fit:cover;border-radius:4px;background:#f5f5f5" />
            <el-tag v-else size="small" type="info">无图片</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="linkUrl" label="链接" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.linkUrl" style="color:#409EFF">{{ row.linkUrl }}</span>
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.enabled" @change="toggleEnabled(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑轮播图' : '新增轮播图'" width="520px" :close-on-click-modal="false">
      <el-form :model="dialog.form" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题' }]">
          <el-input v-model="dialog.form.title" placeholder="轮播图标题" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="dialog.form.imageUrl" placeholder="图片地址（可选，为空时使用默认图）" />
          <div v-if="dialog.form.imageUrl" style="margin-top:8px">
            <img :src="dialog.form.imageUrl" style="max-width:200px;max-height:100px;border-radius:4px;border:1px solid #eee" />
          </div>
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="dialog.form.linkUrl" placeholder="点击跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialog.form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="dialog.form.enabled" />
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
import { bannerApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const formRef = ref(null)

const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { title: '', imageUrl: '', linkUrl: '', sortOrder: 0, enabled: true }
})

async function fetchList() {
  loading.value = true
  try { const r = await bannerApi.list(); list.value = r.data }
  finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false
  dialog.form = { title: '', imageUrl: '', linkUrl: '', sortOrder: 0, enabled: true }
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog._editId = row.id
  dialog.form = { title: row.title, imageUrl: row.imageUrl || '', linkUrl: row.linkUrl || '', sortOrder: row.sortOrder, enabled: row.enabled }
  dialog.visible = true
}

async function save() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) {
      await bannerApi.update(dialog._editId, dialog.form)
      ElMessage.success('更新成功')
    } else {
      await bannerApi.create(dialog.form)
      ElMessage.success('新增成功')
    }
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function toggleEnabled(row) {
  await bannerApi.update(row.id, { ...row, enabled: !row.enabled })
  row.enabled = !row.enabled
  ElMessage.success(row.enabled ? '已启用' : '已禁用')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除轮播图「${row.title}」吗？`, '提示', { type: 'warning' })
  await bannerApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-header { display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:20px; }
.page-header h2 { font-size:18px;font-weight:600;margin-bottom:4px; }
.page-header p { font-size:13px;color:var(--xianbei-text-secondary); }
</style>
```

---

## Task 6 — 前端集成（路由 + API + 菜单 + 顾客端展示）

### 步骤 6.1 — 添加 bannerApi 到 api/index.js

在 `frontend/src/api/index.js` 库存管理 API 后面（第 78 行之后）添加：

```javascript
// ==================== 轮播图管理 ====================
export const bannerApi = {
  list: () => request.get('/banners'),
  create: data => request.post('/banners', data),
  update: (id, data) => request.put(`/banners/${id}`, data),
  delete: id => request.delete(`/banners/${id}`)
}
```

### 步骤 6.2 — 添加路由

在 `frontend/src/router/index.js` 中 `Inventory` 路由后面（第 85 行左右）添加：

```javascript
{
  path: 'banners',
  name: 'Banners',
  component: () => import('../views/Banners.vue'),
  meta: { title: '轮播图管理', role: 'MANAGER' }
}
```

### 步骤 6.3 — 添加侧边栏菜单

在 `frontend/src/layout/MainLayout.vue` 中 "库存管理" 菜单项后面添加：

```html
<el-menu-item index="/banners" v-if="authStore.isManager">
  <el-icon><PictureFilled /></el-icon>
  <span>轮播图管理</span>
</el-menu-item>
```

并导入 `PictureFilled` 图标（从 `@element-plus/icons-vue` 导入）。

### 步骤 6.4 — 添加轮播图组件到顾客点单页 Menu.vue

在 `frontend/src/views/Menu.vue` 中，header 后面添加轮播区域：

```vue
<template>
  <div class="menu-page">
    <!-- 顶部品牌栏 -->
    <header class="menu-header">
      <h1>仙贝奶茶店</h1>
      <p>新鲜手作 · 每一杯都是用心</p>
    </header>

    <!-- 【新增】轮播图区域 -->
    <div class="banner-carousel" v-if="banners.length > 0">
      <el-carousel height="200px" indicator-position="outside" :interval="3000" arrow="always">
        <el-carousel-item v-for="b in banners" :key="b.id">
          <a :href="b.linkUrl || 'javascript:void(0)'" target="_blank" v-if="b.linkUrl" class="banner-link">
            <img :src="b.imageUrl" :alt="b.title" class="banner-img" />
            <div class="banner-title">{{ b.title }}</div>
          </a>
          <div v-else class="banner-link">
            <img :src="b.imageUrl" :alt="b.title" class="banner-img" />
            <div class="banner-title">{{ b.title }}</div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="menu-body">
    <!-- ... 原有内容 ... -->
```

在 `<script setup>` 中添加：

```javascript
// 轮播图
const banners = ref([])

// 在 onMounted 中：
onMounted(async () => {
  const [catRes, prodRes, addonRes, bannerRes] = await Promise.all([
    publicApi.categories(),
    publicApi.products(),
    publicApi.addons(),
    publicApi.banners()  // 新增
  ])
  categories.value = catRes.data
  products.value = prodRes.data
  addons.value = addonRes.data
  banners.value = bannerRes.data || []
  if (categories.value.length > 0) activeCategory.value = categories.value[0].id
})
```

注意：需要确保 `publicApi.banners` 存在。在 `api/index.js` 的 `publicApi` 中添加：

```javascript
banners: () => request.get('/public/banners'),
```

添加 CSS 样式到 Menu.vue 的 `<style scoped>`：

```css
.banner-carousel { max-width: 800px; margin: 16px auto 0; border-radius: 12px; overflow: hidden; }
.banner-link { display: block; position: relative; width: 100%; height: 200px; }
.banner-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.banner-title {
  position: absolute; bottom: 0; left: 0; right: 0;
  padding: 8px 16px; background: linear-gradient(transparent, rgba(0,0,0,0.5));
  color: #fff; font-size: 14px; font-weight: 500;
}
```

---

## Task 7 — 验证

1. 重启后端：确保 Banner 表自动创建
2. 用 Postman 测试 API：
   - `POST /api/banners` 新增轮播图
   - `GET /api/public/banners` 获取启用的轮播图
3. 打开前端登录页，确认新的 Logo 和背景动画
4. 进入后台，确认左侧有「轮播图管理」菜单
5. 新增/编辑/删除轮播图测试
6. 打开顾客点单页 `/menu`，确认轮播图显示且自动播放
7. 打开员工管理页，确认表格行动画

---

## 文件变更总览

### 新增文件（4个）
| # | 文件 | 说明 |
|---|------|------|
| 1 | backend/.../entity/Banner.java | 轮播图实体 |
| 2 | backend/.../repository/BannerRepository.java | 轮播图数据访问 |
| 3 | backend/.../controller/BannerController.java | 轮播图管理 API |
| 4 | frontend/src/views/Banners.vue | 轮播图管理页面 |

### 修改文件（7个）
| # | 文件 | 修改内容 |
|---|------|----------|
| 1 | frontend/src/views/Login.vue | 替换 Logo SVG + 背景装饰 + 蒸汽动画 |
| 2 | frontend/src/layout/MainLayout.vue | 替换 Logo SVG + 添加轮播图菜单项 + 导入新图标 |
| 3 | frontend/src/views/Menu.vue | 添加轮播图组件 + API 调用 + 样式 |
| 4 | frontend/src/views/Users.vue | 添加行动画 + 弹窗弹性动画 + 按钮波纹 |
| 5 | frontend/src/router/index.js | 添加 Banners 路由 |
| 6 | frontend/src/api/index.js | 添加 bannerApi + publicApi.banners |
| 7 | backend/.../PublicController.java | 添加 /api/public/banners 端点 |
