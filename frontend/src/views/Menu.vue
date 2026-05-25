<template>
  <div class="menu-page">
    <!-- ====== 顶部用户菜单 ====== -->
    <div class="top-bar">
      <div class="top-bar-left">
        <svg width="22" height="22" viewBox="0 0 72 72" fill="none" style="flex-shrink:0">
          <path d="M18 30C18 24 22 18 36 18C50 18 54 24 54 30V48C54 54 50 60 36 60C22 60 18 54 18 48V30Z" fill="url(#tbCup)" stroke="#C8925C" stroke-width="2"/>
          <path d="M54 32C60 32 62 38 60 44C58 48 55 47 54 45" stroke="#C8925C" stroke-width="1.8" fill="none" stroke-linecap="round"/>
          <ellipse cx="36" cy="30" rx="18" ry="4" fill="rgba(223,180,132,0.5)"/>
          <defs><linearGradient id="tbCup" x1="36" y1="18" x2="36" y2="60"><stop offset="0%" stop-color="rgba(255,255,255,0.1)"/><stop offset="100%" stop-color="rgba(255,255,255,0.02)"/></linearGradient></defs>
        </svg>
        <span class="top-bar-title">仙贝奶茶</span>
      </div>
      <div class="top-bar-right">
        <template v-if="isLoggedIn">
          <el-dropdown trigger="click" @command="handleUserCmd">
            <span class="user-trigger">
              <el-avatar :size="30" style="background:#FDF6EE;color:#C8925C;font-weight:600;font-size:13px">
                {{ userInfo.realName?.charAt(0) || userInfo.username?.charAt(0) || 'U' }}
              </el-avatar>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人信息</el-dropdown-item>
                <el-dropdown-item command="orders"><el-icon><List /></el-icon>我的订单</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button size="small" text style="color:#fff" @click="showLoginDialog = true">登录</el-button>
          <el-button size="small" plain style="color:#fff;border-color:rgba(255,255,255,0.3)" @click="showRegisterDialog = true">注册</el-button>
        </template>
      </div>
    </div>

    <!-- ====== 轮播图 ====== -->
    <div class="banner-section">
      <div class="banner-track">
        <div
          v-for="(b, idx) in displayBanners"
          :key="b.id"
          class="banner-slide"
          :class="{ active: currentSlide === idx }"
        >
          <div v-if="b.imageUrl" class="banner-img-wrap">
            <img :src="b.imageUrl" :alt="b.title" class="banner-img" @error="onImgError($event)" />
            <div class="banner-overlay">{{ b.title }}</div>
          </div>
          <div v-else class="banner-bg" :style="{ background: b.bgGrad }">
            <div class="banner-deco">
              <svg width="80" height="120" viewBox="0 0 80 120" fill="none">
                <path d="M25 50C25 40 35 30 40 30C45 30 55 40 55 50V90C55 100 45 105 40 105C35 105 25 100 25 90V50Z" stroke="rgba(255,255,255,0.15)" stroke-width="1.5" fill="none"/>
                <ellipse cx="40" cy="50" rx="15" ry="3" fill="rgba(255,255,255,0.06)"/>
              </svg>
            </div>
            <div class="banner-text-wrap">
              <div class="banner-title">{{ b.title }}</div>
              <div class="banner-subtitle" v-if="b.subtitle">{{ b.subtitle }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="banner-dots" v-if="displayBanners.length > 1">
        <span v-for="(b, idx) in displayBanners" :key="idx"
          :class="['dot', { active: currentSlide === idx }]" @click="goTo(idx)"></span>
      </div>
    </div>

    <!-- ====== 下单方式 ====== -->
    <div class="order-type-section">
      <div class="order-type-btn active" @click="orderType = 'dinein'">
        <span class="order-icon">🏪</span>
        <span class="order-label">到店自取</span>
      </div>
      <div class="order-type-btn" @click="orderType = 'delivery'">
        <span class="order-icon">🛵</span>
        <span class="order-label">外卖配送</span>
      </div>
    </div>

    <!-- ====== 菜单正文 ====== -->
    <div class="menu-body">
      <div v-if="combos.length > 0" class="combo-section">
        <div class="section-label">🎯 超值套餐</div>
        <div class="combo-grid">
          <div v-for="item in combos" :key="item.id" class="combo-card" @click="openComboDialog(item)">
            <div class="combo-img">
              <img v-if="item.imageUrl" :src="item.imageUrl" />
              <span v-else>🎯</span>
            </div>
            <div class="combo-info">
              <div class="combo-name">{{ item.name }}</div>
              <div class="combo-desc">
                含 <span v-for="(ci, idx) in (item.items || [])" :key="idx">{{ ci.productName }}x{{ ci.quantity }}<span v-if="idx < item.items.length - 1">、</span></span>
              </div>
              <div class="combo-price">
                <span class="combo-price-num">¥{{ item.price }}</span>
                <span class="combo-badge">套餐</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="category-tabs">
        <button v-for="cat in categories" :key="cat.id"
          :class="['tab-btn', { active: activeCategory === cat.id }]"
          @click="activeCategory = cat.id">{{ cat.name }}</button>
      </div>

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

    <!-- ====== 购物车 ====== -->
    <div v-if="cart.length > 0" class="cart-float" @click="showCart = true">
      <span class="cart-badge">{{ cartTotal }}</span>
      <span>查看购物车 · ¥{{ cartAmount }}</span>
    </div>

    <!-- ====== 登录弹窗 ====== -->
    <el-dialog v-model="showLoginDialog" title="顾客登录" width="360px" :close-on-click-modal="false">
      <el-form ref="loginFormRef" :model="loginForm" label-width="0">
        <el-input v-model="loginForm.username" placeholder="用户名" size="large" style="margin-bottom:12px" />
        <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" show-password style="margin-bottom:12px" />
      </el-form>
      <template #footer>
        <el-button @click="showLoginDialog = false">取消</el-button>
        <el-button type="primary" @click="handleLogin" :loading="loginLoading">登录</el-button>
      </template>
    </el-dialog>

    <!-- ====== 注册弹窗 ====== -->
    <el-dialog v-model="showRegisterDialog" title="顾客注册" width="360px" :close-on-click-modal="false">
      <el-form ref="registerFormRef" :model="registerForm" label-width="0">
        <el-input v-model="registerForm.username" placeholder="用户名" size="large" style="margin-bottom:12px" />
        <el-input v-model="registerForm.password" type="password" placeholder="密码" size="large" show-password style="margin-bottom:12px" />
        <el-input v-model="registerForm.realName" placeholder="姓名" size="large" style="margin-bottom:12px" />
        <el-input v-model="registerForm.phone" placeholder="手机号" size="large" style="margin-bottom:12px" />
      </el-form>
      <template #footer>
        <el-button @click="showRegisterDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" :loading="registerLoading">注册</el-button>
      </template>
    </el-dialog>

    <!-- 点单弹窗 -->
    <el-dialog v-model="orderDialog.visible" title="选择规格" width="380px" :close-on-click-modal="true" destroy-on-close>
      <div class="order-dialog-body">
        <h3>{{ orderDialog.product?.name }}</h3>
        <p class="dialog-price">¥{{ orderDialog.product?.price }}</p>
        <p v-if="orderType === 'delivery'" style="font-size:12px;color:#E6A23C;margin-bottom:8px">🛵 外卖配送</p>
        <div class="form-group"><label>数量</label><el-input-number v-model="orderDialog.quantity" :min="1" :max="99" /></div>
        <div class="form-group"><label>口味备注</label><el-input v-model="orderDialog.tasteNotes" placeholder="如：少糖、少冰、去冰、温" /></div>
        <div class="form-group" v-if="addons.length > 0">
          <label>加料</label>
          <div class="addon-list"><el-checkbox v-for="a in addons" :key="a.id" :label="a.id" @change="toggleAddon(a)">{{ a.name }} (+¥{{ a.price }})</el-checkbox></div>
        </div>
      </div>
      <template #footer>
        <el-button @click="orderDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="addToCart">加入购物车</el-button>
      </template>
    </el-dialog>

    <!-- 套餐点单弹窗 -->
    <el-dialog v-model="comboDialog.visible" title="套餐详情" width="400px" :close-on-click-modal="true" destroy-on-close>
      <div class="order-dialog-body">
        <h3>{{ comboDialog.combo?.name }}</h3>
        <p class="dialog-price">¥{{ comboDialog.combo?.price }}</p>
        <p v-if="orderType === 'delivery'" style="font-size:12px;color:#E6A23C;margin-bottom:8px">🛵 外卖配送</p>
        <div style="background:#FDF6EE;border-radius:8px;padding:12px;margin-bottom:16px">
          <div style="font-size:13px;font-weight:600;color:#A6703E;margin-bottom:8px">套餐内含：</div>
          <div v-for="(ci, idx) in (comboDialog.combo?.items || [])" :key="idx" style="font-size:13px;color:#666;margin-bottom:4px">🧋 {{ ci.productName }} x{{ ci.quantity }}</div>
        </div>
        <div class="form-group"><label>数量</label><el-input-number v-model="comboDialog.quantity" :min="1" :max="99" /></div>
        <div class="form-group"><label>口味备注</label><el-input v-model="comboDialog.tasteNotes" placeholder="如：少糖、少冰、去冰、温" /></div>
      </div>
      <template #footer>
        <el-button @click="comboDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="addComboToCart">加入购物车</el-button>
      </template>
    </el-dialog>

    <!-- 购物车侧栏 -->
    <el-drawer v-model="showCart" :title="orderType === 'delivery' ? '购物车 · 外卖配送' : '购物车 · 到店自取'" size="400px">
      <div class="cart-items">
        <div v-for="(item, idx) in cart" :key="idx" class="cart-item">
          <div class="cart-item-info">
            <strong>{{ item.name }}</strong><span>x{{ item.quantity }}</span>
            <span class="cart-item-notes" v-if="item.tasteNotes">备注：{{ item.tasteNotes }}</span>
            <span class="cart-item-notes" v-if="item.addonText">加料：{{ item.addonText }}</span>
          </div>
          <div class="cart-item-price">¥{{ item.subtotal }}</div>
          <el-button size="small" type="danger" text @click="removeFromCart(idx)">删除</el-button>
        </div>
        <div v-if="cart.length === 0" style="text-align:center;padding:40px 0;color:#999">购物车是空的</div>
      </div>
      <div class="cart-footer" v-if="cart.length > 0">
        <div class="cart-total"><span>合计</span><strong>¥{{ cartAmount }}</strong></div>
        <el-form :model="customerForm" label-width="0" class="customer-form">
          <el-input v-model="customerForm.name" placeholder="您的姓名" style="margin-bottom:10px" />
          <el-input v-model="customerForm.phone" placeholder="手机号" style="margin-bottom:10px" />
          <el-input v-model="customerForm.remark" placeholder="订单备注（可选）" />
        </el-form>
        <el-button type="primary" size="large" style="width:100%;margin-top:12px" @click="submitOrder" :loading="submitting">
          {{ orderType === 'delivery' ? '提交外卖订单' : '提交订单 · 到店自取' }}
        </el-button>
      </div>
    </el-drawer>

    <!-- 下单成功弹窗 -->
    <el-dialog v-model="successDialog.visible" title="下单成功" width="360px" :close-on-click-modal="false">
      <div class="success-body">
        <div class="success-icon">✅</div>
        <h2>取餐号：{{ successDialog.orderNo }}</h2>
        <p>{{ orderType === 'delivery' ? '请耐心等待配送' : '请留意叫号，凭取餐号取餐' }}</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="resetOrder" style="width:100%">继续点单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue'
import { publicApi, customerApi } from '../api'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { User, List, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()

// ====== 菜单数据 ======
const categories = ref([])
const products = ref([])
const addons = ref([])
const banners = ref([])
const combos = ref([])
const activeCategory = ref(null)
const cart = ref([])
const showCart = ref(false)
const submitting = ref(false)
const orderType = ref('dinein')

const customerForm = reactive({ name: '', phone: '', remark: '' })
const orderDialog = reactive({ visible: false, product: null, quantity: 1, tasteNotes: '', selectedAddons: [] })
const comboDialog = reactive({ visible: false, combo: null, quantity: 1, tasteNotes: '' })
const successDialog = reactive({ visible: false, orderNo: '' })

// ====== 用户认证 ======
const isLoggedIn = ref(!!localStorage.getItem('token'))
const userInfo = ref(JSON.parse(localStorage.getItem('customerInfo') || '{}'))
const showLoginDialog = ref(false)
const showRegisterDialog = ref(false)
const loginLoading = ref(false)
const registerLoading = ref(false)
const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', password: '', realName: '', phone: '' })

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) return ElMessage.warning('请填写完整')
  loginLoading.value = true
  try {
    const res = await customerApi.login(loginForm)
    const data = res.data
    localStorage.setItem('token', data.token)
    localStorage.setItem('customerInfo', JSON.stringify(data))
    isLoggedIn.value = true
    userInfo.value = data
    showLoginDialog.value = false
    ElMessage.success('登录成功')
  } catch { /* handled */ }
  finally { loginLoading.value = false }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password) return ElMessage.warning('请填写完整')
  registerLoading.value = true
  try {
    await customerApi.register(registerForm)
    ElMessage.success('注册成功，请登录')
    showRegisterDialog.value = false
    loginForm.username = registerForm.username
    showLoginDialog.value = true
  } catch { /* handled */ }
  finally { registerLoading.value = false }
}

function handleUserCmd(cmd) {
  if (cmd === 'profile') router.push('/customer/profile')
  else if (cmd === 'orders') router.push('/customer/orders')
  else if (cmd === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('customerInfo')
    isLoggedIn.value = false
    userInfo.value = {}
    ElMessage.success('已退出')
  }
}

// ====== 轮播图 ======
const currentSlide = ref(0)
let slideTimer = null

const DEFAULT_BANNERS = [
  { id: '__d1', title: '新鲜手作', subtitle: '每一杯都是用心', bgGrad: 'linear-gradient(135deg, #1C2536, #2C3E50)' },
  { id: '__d2', title: '新品上市', subtitle: '限时特惠 欢迎品尝', bgGrad: 'linear-gradient(135deg, #2C1810, #4A2C1A)' },
  { id: '__d3', title: '会员专享', subtitle: '积分兑换 好礼不断', bgGrad: 'linear-gradient(135deg, #1A2A1A, #2C3E2C)' }
]

const displayBanners = computed(() => {
  if (banners.value.length > 0) {
    return banners.value.map(b => ({
      ...b,
      bgGrad: b.imageUrl ? undefined : 'linear-gradient(135deg, #1C2536, #2C3E50)',
      imageUrl: b.imageUrl || undefined
    }))
  }
  return DEFAULT_BANNERS
})

function goTo(idx) { currentSlide.value = idx; resetSlideTimer() }
function onImgError(event) { event.target.style.display = 'none' }

function nextSlide() {
  if (displayBanners.value.length < 2) return
  currentSlide.value = (currentSlide.value + 1) % displayBanners.value.length
}
function resetSlideTimer() {
  if (slideTimer) clearInterval(slideTimer)
  slideTimer = setInterval(nextSlide, 4000)
}

// ====== 菜单逻辑 ======
const filteredProducts = computed(() => {
  if (!activeCategory.value) return products.value
  return products.value.filter(p => p.categoryId === activeCategory.value)
})

const cartTotal = computed(() => cart.value.reduce((s, i) => s + i.quantity, 0))
const cartAmount = computed(() => cart.value.reduce((s, i) => s + i.subtotal, 0).toFixed(2))

function openOrderDialog(product) {
  orderDialog.product = product; orderDialog.quantity = 1
  orderDialog.tasteNotes = ''; orderDialog.selectedAddons = []
  orderDialog.visible = true
}
function openComboDialog(combo) {
  comboDialog.combo = combo; comboDialog.quantity = 1; comboDialog.tasteNotes = ''
  comboDialog.visible = true
}
function toggleAddon(addon) {
  const idx = orderDialog.selectedAddons.indexOf(addon.id)
  if (idx > -1) orderDialog.selectedAddons.splice(idx, 1)
  else orderDialog.selectedAddons.push(addon.id)
}

function addToCart() {
  const p = orderDialog.product
  const selectedAddons = orderDialog.selectedAddons.map(id => addons.value.find(x => x.id === id)).filter(Boolean)
  const addonText = selectedAddons.map(a => `${a.name}+¥${a.price}`).join('、')
  const addonTotal = selectedAddons.reduce((s, a) => s + Number(a.price), 0)
  cart.value.push({
    productId: p.id, name: p.name, quantity: orderDialog.quantity,
    price: p.price, tasteNotes: orderDialog.tasteNotes, addonText,
    addonIds: [...orderDialog.selectedAddons],
    subtotal: ((p.price + addonTotal) * orderDialog.quantity).toFixed(2)
  })
  orderDialog.visible = false; ElMessage.success('已加入购物车')
}

function addComboToCart() {
  const c = comboDialog.combo
  cart.value.push({
    productId: `combo_${c.id}`, name: `[套餐] ${c.name}`, quantity: comboDialog.quantity,
    price: c.price, tasteNotes: comboDialog.tasteNotes,
    addonText: c.items?.map(i => `${i.productName}x${i.quantity}`).join('、') || '',
    subtotal: (c.price * comboDialog.quantity).toFixed(2), isCombo: true
  })
  comboDialog.visible = false; ElMessage.success('套餐已加入购物车')
}

function removeFromCart(idx) { cart.value.splice(idx, 1); if (cart.value.length === 0) showCart.value = false }

async function submitOrder() {
  if (!customerForm.name) return ElMessage.warning('请填写姓名')
  if (!customerForm.phone) return ElMessage.warning('请填写手机号')
  submitting.value = true
  try {
    const items = cart.value.map(i => ({
      productId: i.productId, quantity: i.quantity,
      tasteNotes: i.tasteNotes || '', addons: i.addonText || '',
      addonIds: i.addonIds || []
    }))
    const res = await publicApi.createOrder({
      customerName: customerForm.name, customerPhone: customerForm.phone,
      remark: customerForm.remark || '', orderType: orderType.value, items
    })
    successDialog.orderNo = res.data.orderNo; successDialog.visible = true; showCart.value = false
  } finally { submitting.value = false }
}

function resetOrder() {
  successDialog.visible = false; cart.value = []
  customerForm.name = ''; customerForm.phone = ''; customerForm.remark = ''
}

onMounted(async () => {
  const [catRes, prodRes, addonRes] = await Promise.all([
    publicApi.categories(), publicApi.products(), publicApi.addons()
  ])
  categories.value = catRes.data; products.value = prodRes.data; addons.value = addonRes.data
  if (categories.value.length > 0) activeCategory.value = categories.value[0].id
  try { const r = await publicApi.banners(); banners.value = r.data || [] } catch { banners.value = [] }
  try { const r = await publicApi.combos(); combos.value = r.data || [] } catch { combos.value = [] }
  resetSlideTimer()
})

onUnmounted(() => { if (slideTimer) clearInterval(slideTimer) })
</script>

<style scoped>
.menu-page { min-height: 100vh; background: #f8f6f0; padding-bottom: 80px; }

/* ====== 顶部栏 ====== */
.top-bar {
  background: linear-gradient(135deg, #1C2536, #2C3E50);
  display: flex; justify-content: space-between; align-items: center;
  padding: 10px 16px; position: sticky; top: 0; z-index: 200;
}
.top-bar-left { display: flex; align-items: center; gap: 8px; }
.top-bar-title { color: #fff; font-size: 16px; font-weight: 600; letter-spacing: 1px; }
.top-bar-right { display: flex; align-items: center; gap: 6px; }
.user-trigger { cursor: pointer; display: flex; align-items: center; }

/* ====== 轮播图 ====== */
.banner-section { max-width: 800px; margin: 0 auto; padding: 0 12px; position: relative; margin-top: 12px; }
.banner-track { position: relative; width: 100%; height: 180px; border-radius: 12px; overflow: hidden; background: #1C2536; }
.banner-slide { position: absolute; inset: 0; opacity: 0; transition: opacity 0.5s ease; pointer-events: none; }
.banner-slide.active { opacity: 1; pointer-events: auto; }
.banner-img-wrap { width: 100%; height: 100%; position: relative; background: #1C2536; }
.banner-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.banner-overlay { position: absolute; bottom: 0; left: 0; right: 0; padding: 10px 16px; background: linear-gradient(transparent, rgba(0,0,0,0.55)); color: #fff; font-size: 15px; font-weight: 500; }
.banner-bg { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; position: relative; }
.banner-deco { position: absolute; right: 40px; top: 50%; transform: translateY(-50%); opacity: 0.5; }
.banner-text-wrap { text-align: center; z-index: 1; }
.banner-title { color: #fff; font-size: 28px; font-weight: 600; letter-spacing: 6px; text-shadow: 0 2px 8px rgba(0,0,0,0.2); }
.banner-subtitle { color: rgba(255,255,255,0.6); font-size: 14px; letter-spacing: 4px; margin-top: 10px; }
.banner-dots { display: flex; justify-content: center; gap: 6px; padding: 10px 0 6px; }
.banner-dots .dot { width: 8px; height: 8px; border-radius: 50%; background: #ddd; cursor: pointer; transition: all 0.3s; }
.banner-dots .dot.active { width: 24px; border-radius: 4px; background: #C8925C; }

/* ====== 下单方式 ====== */
.order-type-section { max-width: 800px; margin: 8px auto 0; padding: 0 12px; display: flex; gap: 10px; }
.order-type-btn {
  flex: 1; display: flex; align-items: center; justify-content: center;
  gap: 8px; padding: 14px; border-radius: 12px; background: #fff;
  border: 2px solid #eee; cursor: pointer; transition: all 0.25s; font-size: 15px; font-weight: 500;
}
.order-type-btn:hover { border-color: #C8925C; background: #FDF6EE; }
.order-type-btn.active { border-color: #C8925C; background: #FDF6EE; color: #A6703E; }
.order-icon { font-size: 22px; }

/* ====== 菜单正文 ====== */
.menu-body { max-width: 800px; margin: 0 auto; padding: 16px 12px; }
.combo-section { margin-bottom: 16px; }
.section-label { font-size: 15px; font-weight: 600; color: #A6703E; margin-bottom: 10px; border-left: 3px solid #C8925C; padding-left: 10px; }
.combo-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.combo-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; border: 1px solid #F0E8D8; transition: all 0.2s; display: flex; flex-direction: column; }
.combo-card:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(200,146,92,0.15); }
.combo-img { width: 100%; height: 100px; background: #FDF6EE; display: flex; align-items: center; justify-content: center; font-size: 32px; }
.combo-img img { width: 100%; height: 100%; object-fit: cover; }
.combo-info { padding: 10px 12px 12px; }
.combo-name { font-size: 14px; font-weight: 600; margin-bottom: 4px; }
.combo-desc { font-size: 12px; color: #999; margin-bottom: 6px; line-height: 1.4; }
.combo-price { display: flex; align-items: center; gap: 6px; }
.combo-price-num { font-size: 17px; font-weight: 700; color: #C8925C; }
.combo-badge { font-size: 10px; background: #C8925C; color: #fff; padding: 1px 6px; border-radius: 4px; }
@media (min-width: 640px) { .combo-grid { grid-template-columns: repeat(3, 1fr); } }

.category-tabs { display: flex; gap: 8px; overflow-x: auto; padding: 8px 0; margin-bottom: 16px; }
.tab-btn { padding: 8px 20px; border-radius: 20px; border: 1px solid #ddd; background: #fff; font-size: 14px; cursor: pointer; white-space: nowrap; transition: all 0.2s; }
.tab-btn.active { background: #C8925C; color: #fff; border-color: #C8925C; }

.product-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.product-card { background: #fff; border-radius: 12px; padding: 16px; cursor: pointer; display: flex; gap: 12px; align-items: center; box-shadow: 0 2px 8px rgba(0,0,0,0.04); transition: transform 0.2s, box-shadow 0.2s; }
.product-card:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
.product-icon { width: 48px; height: 48px; border-radius: 10px; background: #FDF6EE; display: flex; align-items: center; justify-content: center; font-size: 20px; color: #C8925C; font-weight: 700; flex-shrink: 0; }
.product-info h3 { font-size: 15px; font-weight: 600; margin-bottom: 2px; }
.product-desc { font-size: 12px; color: #999; margin-bottom: 2px; }
.product-price { font-size: 16px; font-weight: 700; color: #C8925C; }

.cart-float { position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); background: #1C2536; color: #fff; padding: 12px 24px; border-radius: 28px; cursor: pointer; display: flex; align-items: center; gap: 8px; font-size: 15px; box-shadow: 0 4px 20px rgba(0,0,0,0.2); z-index: 100; }
.cart-badge { width: 24px; height: 24px; border-radius: 50%; background: #C8925C; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700; }

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
@media (min-width: 640px) { .product-grid { grid-template-columns: repeat(3, 1fr); } }
</style>
