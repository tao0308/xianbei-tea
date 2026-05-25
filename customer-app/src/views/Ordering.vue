<template>
  <div class="ordering-page">
    <!-- 轮播图 -->
    <div class="store-header">
      <div class="banner-track" @click="nextSlide">
        <div
          v-for="(b, idx) in displayBanners"
          :key="b.id"
          class="banner-slide"
          :class="{ active: currentSlide === idx }"
          @click.stop="goTo(idx)"
        >
          <div v-if="b.imageUrl" class="banner-img-wrap">
            <img :src="b.imageUrl" :alt="b.title" class="banner-img" @error="onBannerError($event)" />
          </div>
          <div v-else class="banner-bg" :style="{ background: b.bgGrad }">
            <div class="banner-text">
              <div class="banner-title">{{ b.title }}</div>
              <div class="banner-subtitle" v-if="b.subtitle">{{ b.subtitle }}</div>
            </div>
          </div>
        </div>
        <!-- 左右切换箭头 -->
        <div class="banner-arrow banner-arrow-left" @click.stop="goTo(prevIndex)">
          <svg width="20" height="20" viewBox="0 0 20 20"><path d="M12 4L6 10L12 16" stroke="rgba(255,255,255,0.5)" stroke-width="2" fill="none" stroke-linecap="round"/></svg>
        </div>
        <div class="banner-arrow banner-arrow-right" @click.stop="goTo(nextIndex)">
          <svg width="20" height="20" viewBox="0 0 20 20"><path d="M8 4L14 10L8 16" stroke="rgba(255,255,255,0.5)" stroke-width="2" fill="none" stroke-linecap="round"/></svg>
        </div>
      </div>
      <div class="banner-dots">
        <span v-for="(b, idx) in displayBanners" :key="idx"
          :class="['dot', { active: currentSlide === idx }]" @click.stop="goTo(idx)"></span>
      </div>
      <!-- 店铺名浮层 -->
      <div class="store-overlay">
        <h1 class="store-title">仙贝奶茶店</h1>
        <p class="store-subtitle">新鲜手作 · 每一杯都是用心</p>
      </div>
    </div>

    <!-- 自取/外卖切换 -->
    <div class="order-type-bar">
      <van-tabs v-model:active="orderType" color="#C8925C" sticky offset-top="0">
        <van-tab title="🥤 到店自取" name="PICKUP" />
        <van-tab title="🛵 外卖配送" name="DELIVERY" />
      </van-tabs>
    </div>

    <!-- 分类和菜单 -->
    <div class="menu-area">
      <div class="category-side">
        <div :class="['cat-item', 'cat-combo', { active: activeCat === 'combo' }]"
          @click="activeCat = 'combo'">🎯 套餐</div>
        <div v-for="cat in categories" :key="cat.id"
          :class="['cat-item', { active: activeCat === cat.id }]"
          @click="activeCat = cat.id">{{ cat.name }}</div>
      </div>
      <div class="product-list">
        <!-- 套餐列表 -->
        <template v-if="activeCat === 'combo'">
          <div v-for="item in combos" :key="item.id" class="product-item combo-product" @click="openComboDetail(item)">
            <div v-if="item.imageUrl" class="product-img">
              <img :src="item.imageUrl" :alt="item.name" />
            </div>
            <div v-else class="product-icon">🎯</div>
            <div class="product-info">
              <div class="product-name">{{ item.name }}</div>
              <div class="product-desc">
                含 <span v-for="(ci, idx) in (item.items || [])" :key="idx">{{ ci.productName }}x{{ ci.quantity }}<span v-if="idx < item.items.length - 1">、</span></span>
              </div>
              <div class="product-price">¥{{ item.price }}</div>
            </div>
            <van-icon name="add-circle" size="22" color="#C8925C" class="add-btn" @click.stop="quickAddCombo(item)" />
          </div>
          <div v-if="combos.length === 0" style="text-align:center;padding:40px 0;color:#999">暂无套餐</div>
        </template>
        <!-- 单品列表 -->
        <template v-else>
          <div v-for="item in filteredProducts" :key="item.id" class="product-item" @click="showDetail(item)">
          <div v-if="!item._imgErr && (item.imageUrl || item.image_url)" class="product-img">
            <img :src="item.imageUrl || item.image_url" :alt="item.name" @error="item._imgErr = true" />
          </div>
          <div v-else class="product-icon">{{ item.name.charAt(0) }}</div>
          <div class="product-info">
            <div class="product-name">{{ item.name }}</div>
            <div class="product-desc" v-if="item.description">{{ item.description }}</div>
            <div class="product-price">¥{{ item.price }}</div>
          </div>
          <van-icon name="add-circle" size="22" color="#C8925C" class="add-btn" @click.stop="quickAdd(item)" />
        </div>
        <div v-if="filteredProducts.length === 0" style="text-align:center;padding:40px 0;color:#999">暂无饮品</div>
        </template>
      </div>
    </div>

    <!-- 购物车底栏 -->
    <div class="cart-bar" v-if="cart.length > 0">
      <div class="cart-info" @click="showCart = true">
        <div class="cart-icon">
          <van-icon name="cart-o" size="22" color="#fff" />
          <span class="cart-badge">{{ cartTotal }}</span>
        </div>
        <div class="cart-price">¥{{ totalAmount }}</div>
      </div>
      <div class="cart-btn" @click="showCart = true">去结算</div>
    </div>

    <!-- 点单弹窗 -->
    <van-action-sheet v-model:show="detailShow" title="选择规格">
      <div style="padding: 16px">
        <h3>{{ detailProduct?.name }}</h3>
        <p style="color:#C8925C;font-size:18px;font-weight:700;margin:8px 0 16px">¥{{ detailProduct?.price }}</p>
        <van-stepper v-model="detailQty" min="1" max="99" />

        <!-- 温度选择 -->
        <div style="margin-top:14px">
          <div style="font-size:13px;color:#666;margin-bottom:8px;font-weight:500">温度</div>
          <div class="opt-group">
            <span v-for="t in tempOptions" :key="t"
              :class="['opt-chip', { active: detailTemp === t }]"
              @click="detailTemp = t">{{ t }}</span>
          </div>
        </div>

        <!-- 甜度选择 -->
        <div style="margin-top:12px">
          <div style="font-size:13px;color:#666;margin-bottom:8px;font-weight:500">甜度</div>
          <div class="opt-group">
            <span v-for="s in sweetOptions" :key="s"
              :class="['opt-chip', { active: detailSweet === s }]"
              @click="detailSweet = s">{{ s }}</span>
          </div>
        </div>

        <!-- 加料选择 -->
        <div v-if="addons.length > 0" style="margin-top:14px">
          <div style="font-size:13px;color:#666;margin-bottom:8px;font-weight:500">选择加料</div>
          <div class="addon-grid">
            <div v-for="a in addons" :key="a.id"
              :class="['addon-chip', { selected: detailAddonIds.includes(a.id) }]"
              @click="toggleAddon(a.id)">
              <span>{{ a.name }}</span>
              <span style="font-size:11px;color:#C8925C">+¥{{ a.price }}</span>
            </div>
          </div>
        </div>

        <van-button round block color="#C8925C" style="margin-top:16px" @click="confirmDetail">加入购物车</van-button>
      </div>
    </van-action-sheet>

    <!-- 套餐点单弹窗 -->
    <van-action-sheet v-model:show="comboDetailShow" title="套餐详情">
      <div style="padding:16px">
        <h3>{{ comboDetail?.name }}</h3>
        <p style="color:#C8925C;font-size:18px;font-weight:700;margin:8px 0 16px">¥{{ comboDetail?.price }}</p>
        <div style="background:#FDF6EE;border-radius:8px;padding:12px;margin-bottom:12px">
          <div style="font-size:13px;font-weight:600;color:#A6703E;margin-bottom:6px">套餐内含：</div>
          <div v-for="(ci, idx) in (comboDetail?.items || [])" :key="idx" style="font-size:13px;color:#666;margin-bottom:3px">🧋 {{ ci.productName }} x{{ ci.quantity }}</div>
        </div>
        <van-stepper v-model="comboQty" min="1" max="99" />
        <div style="margin-top:14px">
          <div style="font-size:13px;color:#666;margin-bottom:8px;font-weight:500">温度</div>
          <div class="opt-group">
            <span v-for="t in tempOptions" :key="t"
              :class="['opt-chip', { active: comboTemp === t }]"
              @click="comboTemp = t">{{ t }}</span>
          </div>
        </div>
        <div style="margin-top:12px">
          <div style="font-size:13px;color:#666;margin-bottom:8px;font-weight:500">甜度</div>
          <div class="opt-group">
            <span v-for="s in sweetOptions" :key="s"
              :class="['opt-chip', { active: comboSweet === s }]"
              @click="comboSweet = s">{{ s }}</span>
          </div>
        </div>
        <van-button round block color="#C8925C" style="margin-top:16px" @click="confirmComboDetail">加入购物车</van-button>
      </div>
    </van-action-sheet>

    <!-- 购物车弹窗 -->
    <van-action-sheet v-model:show="showCart" title="购物车">
      <div style="padding: 16px">
        <div v-for="(item, idx) in cart" :key="idx" style="display:flex;align-items:center;padding:8px 0;border-bottom:1px solid #f0f0f0">
          <div style="flex:1">
            <div style="font-weight:500">{{ item.name }} x{{ item.quantity }}</div>
            <div v-if="item.tasteNotes" style="font-size:12px;color:#999">{{ item.tasteNotes }}</div>
            <div v-if="item.addonText" style="font-size:12px;color:#C8925C">加料：{{ item.addonText }}</div>
          </div>
          <div style="color:#C8925C;font-weight:600;margin-right:12px">¥{{ item.itemTotal || (item.price * item.quantity).toFixed(2) }}</div>
          <van-icon name="delete" size="18" color="#999" @click="removeFromCart(idx)" />
        </div>

        <!-- 配送费 -->
        <div v-if="orderType === 'DELIVERY'" style="display:flex;justify-content:space-between;padding:10px 0 4px;font-size:13px;color:#666;border-bottom:1px solid #f0f0f0">
          <span>🛵 配送费</span>
          <span style="font-weight:500">¥5.00</span>
        </div>

        <!-- 合计 -->
        <div style="display:flex;justify-content:space-between;padding:10px 0 4px;font-size:15px;font-weight:600">
          <span>合计</span>
          <span style="color:#C8925C;font-size:18px">¥{{ totalWithDelivery }}</span>
        </div>
        <div v-if="orderType === 'DELIVERY'" style="margin-top:12px">
          <van-field v-model="deliveryAddress" label="送餐地址" placeholder="请填写送餐地址" readonly @click="pickAddress" right-icon="location-o" />
          <van-button v-if="isLoggedIn" text size="small" color="#C8925C" style="margin-top:4px" @click="goAddresses">📋 从地址簿选择</van-button>
        </div>
        <div style="display:flex;gap:8px;margin-top:12px" v-if="!isLoggedIn">
          <van-button round block plain color="#C8925C" @click="goRegister">注册</van-button>
          <van-button round block color="#C8925C" @click="goLogin">登录后下单</van-button>
        </div>
        <van-button v-else round block color="#C8925C" style="margin-top:12px" :loading="submitting" @click="submitOrder">提交订单</van-button>
      </div>
    </van-action-sheet>

    <!-- 下单成功 -->
    <van-action-sheet v-model:show="successShow" title="下单成功">
      <div style="text-align:center;padding:30px 16px">
        <div style="font-size:48px;margin-bottom:12px">✅</div>
        <h2 style="color:#C8925C;font-size:24px">取餐号：{{ successOrderNo }}</h2>
        <p style="color:#999;margin-top:8px">{{ orderType === 'PICKUP' ? '请留意叫号取餐' : '请等待配送' }}</p>
        <van-button round block color="#C8925C" style="margin-top:20px" @click="resetOrder">继续点单</van-button>
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { customerApi } from '../api'
import { showToast } from 'vant'

const router = useRouter()
const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn)

const categories = ref([])
const products = ref([])
const activeCat = ref(null)
const orderType = ref('PICKUP')
const cart = ref([])
const showCart = ref(false)
const submitting = ref(false)
const showPayment = ref(false)
const deliveryAddress = ref('')
const detailShow = ref(false)
const detailProduct = ref(null)
const detailQty = ref(1)
const detailAddonIds = ref([])
const addons = ref([])
const combos = ref([])
const comboDetailShow = ref(false)
const comboDetail = ref(null)
const comboQty = ref(1)
const comboTemp = ref('')
const comboSweet = ref('')
const successShow = ref(false)
const successOrderNo = ref('')

// 口味选项
const tempOptions = ['少冰', '正常冰', '常温', '加热']
const sweetOptions = ['七分甜', '正常甜', '五分甜', '三分甜']
const detailTemp = ref('正常冰')
const detailSweet = ref('正常甜')

// ====== 轮播图 ======
const banners = ref([])
const currentSlide = ref(0)
let slideTimer = null

const DEFAULT_BANNERS = [
  { id: '__d1', title: '新鲜手作', subtitle: '每一杯都是用心', bgGrad: 'linear-gradient(135deg, #1C2536, #2C3E50)' },
  { id: '__d2', title: '新品上市', subtitle: '限时特惠 欢迎品尝', bgGrad: 'linear-gradient(135deg, #2C1810, #4A2C1A)' },
  { id: '__d3', title: '会员专享', subtitle: '积分兑换 好礼不断', bgGrad: 'linear-gradient(135deg, #1A2A1A, #2C3E2C)' }
]

const displayBanners = computed(() => {
  if (banners.value.length > 0) {
    return banners.value.map(b => ({ ...b, bgGrad: b.imageUrl ? undefined : 'linear-gradient(135deg, #1C2536, #2C3E50)', imageUrl: b.imageUrl || undefined }))
  }
  return DEFAULT_BANNERS
})

const prevIndex = computed(() => (currentSlide.value - 1 + displayBanners.value.length) % displayBanners.value.length)
const nextIndex = computed(() => (currentSlide.value + 1) % displayBanners.value.length)

function goTo(idx) { currentSlide.value = idx; resetSlideTimer() }
function nextSlide() {
  if (displayBanners.value.length < 2) return
  currentSlide.value = (currentSlide.value + 1) % displayBanners.value.length
  resetSlideTimer()
}
function resetSlideTimer() { if (slideTimer) clearInterval(slideTimer); slideTimer = setInterval(nextSlide, 4500) }
function onBannerError(event) { event.target.style.display = 'none' }

const filteredProducts = computed(() => {
  if (!activeCat.value || activeCat.value === 'combo') return products.value
  return products.value.filter(p => p.categoryId === activeCat.value)
})

const cartTotal = computed(() => cart.value.reduce((s, i) => s + i.quantity, 0))
const totalAmount = computed(() => cart.value.reduce((s, i) => s + Number(i.itemTotal || (i.price * i.quantity)), 0).toFixed(2))
const totalWithDelivery = computed(() => {
  const items = cart.value.reduce((s, i) => s + Number(i.itemTotal || (i.price * i.quantity)), 0)
  const fee = orderType.value === 'DELIVERY' ? 5 : 0
  return (items + fee).toFixed(2)
})

function quickAdd(product) {
  const existing = cart.value.find(i => i.productId === product.id && !i.tasteNotes)
  if (existing) { existing.quantity++ }
  else {
    cart.value.push({ productId: product.id, name: product.name, quantity: 1, price: product.price, tasteNotes: '' })
  }
  showToast('已加入购物车')
}

function openComboDetail(combo) {
  comboDetail.value = combo; comboQty.value = 1;
  comboTemp.value = '正常冰'; comboSweet.value = '正常甜';
  comboDetailShow.value = true
}

function quickAddCombo(combo) {
  const comboAddonIds = collectComboAddonIds(combo)
  cart.value.push({
    productId: null, name: `[套餐] ${combo.name}`,
    quantity: 1, price: combo.price, tasteNotes: '正常冰 / 正常甜',
    addonText: combo.items?.map(i => `${i.productName}x${i.quantity}`).join('、') || '',
    isCombo: true, comboId: combo.id,
    addonIds: comboAddonIds,
    itemTotal: combo.price.toFixed(2)
  })
  showToast('套餐已加入购物车')
}

function confirmComboDetail() {
  const c = comboDetail.value
  const tasteText = `${comboTemp.value} / ${comboSweet.value}`
  const comboAddonIds = collectComboAddonIds(c)
  cart.value.push({
    productId: null, name: `[套餐] ${c.name}`,
    quantity: comboQty.value, price: c.price, tasteNotes: tasteText,
    addonText: c.items?.map(i => `${i.productName}x${i.quantity}`).join('、') || '',
    isCombo: true, comboId: c.id,
    addonIds: comboAddonIds,
    itemTotal: (c.price * comboQty.value).toFixed(2)
  })
  comboDetailShow.value = false; showToast('套餐已加入购物车')
}

/** 从套餐的 items 中提取所有加料 ID（去重） */
function collectComboAddonIds(combo) {
  const ids = new Set()
  ;(combo.items || []).forEach(ci => {
    if (ci.addonIds) {
      try { JSON.parse(ci.addonIds).forEach(id => ids.add(id)) } catch {}
    }
  })
  return [...ids]
}

function showDetail(product) {
  detailProduct.value = product; detailQty.value = 1;
  detailTemp.value = '正常冰'; detailSweet.value = '正常甜';
  detailAddonIds.value = []; detailShow.value = true
}

function toggleAddon(id) {
  const idx = detailAddonIds.value.indexOf(id)
  if (idx > -1) detailAddonIds.value.splice(idx, 1)
  else detailAddonIds.value.push(id)
}

function confirmDetail() {
  const p = detailProduct.value
  const tasteText = `${detailTemp.value} / ${detailSweet.value}`
  const selectedAddons = detailAddonIds.value.map(id => addons.value.find(a => a.id === id)).filter(Boolean)
  const addonText = selectedAddons.map(a => `${a.name}+¥${a.price}`).join('、')
  const addonTotal = selectedAddons.reduce((s, a) => s + Number(a.price), 0)
  const existing = cart.value.find(i => i.productId === p.id && i.tasteNotes === tasteText && JSON.stringify(i.addonIds) === JSON.stringify(detailAddonIds.value))
  if (existing) {
    existing.quantity += detailQty.value
    existing.itemTotal = ((p.price + addonTotal) * existing.quantity).toFixed(2)
  } else {
    cart.value.push({
      productId: p.id, name: p.name, quantity: detailQty.value, price: p.price,
      tasteNotes: tasteText, addonText, addonIds: [...detailAddonIds.value],
      itemTotal: ((p.price + addonTotal) * detailQty.value).toFixed(2)
    })
  }
  detailShow.value = false; showToast('已加入购物车')
}

function removeFromCart(idx) { cart.value.splice(idx, 1) }

function goLogin() { router.push('/login') }
function goRegister() { router.push('/register') }
function goAddresses() {
  // 保存订单状态到 sessionStorage，回来后恢复
  sessionStorage.setItem('order_cart', JSON.stringify(cart.value))
  sessionStorage.setItem('order_type', orderType.value)
  sessionStorage.setItem('order_address', deliveryAddress.value || '')
  router.push('/addresses')
}
function pickAddress() { router.push('/addresses') }

async function submitOrder() {
  if (orderType.value === 'DELIVERY' && !deliveryAddress.value) {
    return showToast('请填写送餐地址')
  }
  submitting.value = true
  try {
    const deliveryFee = orderType.value === 'DELIVERY' ? 5 : 0
    const items = cart.value.map(i => ({
      productId: i.isCombo ? 0 : i.productId, quantity: i.quantity,
      tasteNotes: i.tasteNotes || '', addons: i.addonText || '',
      addonIds: i.addonIds || [], isCombo: !!i.isCombo,
      productName: i.name, price: i.price
    }))
    const res = await customerApi.createOrder({
      customerName: authStore.realName || authStore.username,
      customerPhone: '',
      orderType: orderType.value,
      address: orderType.value === 'DELIVERY' ? deliveryAddress.value : '',
      paymentMethod: 'MOCK',
      deliveryFee: deliveryFee,
      items
    })
    successOrderNo.value = res.data.orderNo; successShow.value = true; showCart.value = false
  } catch (e) { showToast(e.message || '下单失败') }
  finally { submitting.value = false }
}

function resetOrder() { successShow.value = false; cart.value = []; deliveryAddress.value = '' }

onMounted(async () => {
  // 从地址管理回来后恢复购物车和地址
  const savedCart = sessionStorage.getItem('order_cart')
  if (savedCart) {
    try { cart.value = JSON.parse(savedCart) } catch {}
    sessionStorage.removeItem('order_cart')
    // 恢复配送方式和地址
    const savedType = sessionStorage.getItem('order_type')
    if (savedType) orderType.value = savedType
    sessionStorage.removeItem('order_type')
    const savedAddr = sessionStorage.getItem('order_address')
    if (savedAddr) deliveryAddress.value = savedAddr
    sessionStorage.removeItem('order_address')
    // 从地址簿选中的地址
    const selectedAddr = sessionStorage.getItem('order_selected_address')
    if (selectedAddr) {
      try { deliveryAddress.value = JSON.parse(selectedAddr).address } catch {}
      sessionStorage.removeItem('order_selected_address')
    }
    if (cart.value.length > 0) showCart.value = true  // 自动打开购物车
  }

  const [catRes, prodRes, addonRes] = await Promise.all([
    customerApi.categories(), customerApi.products(), customerApi.addons()
  ])
  categories.value = catRes.data; products.value = prodRes.data; addons.value = addonRes.data || []
  if (categories.value.length > 0) activeCat.value = categories.value[0].id
  try { const r = await customerApi.banners(); banners.value = r.data || [] } catch { banners.value = [] }
  try { const r = await customerApi.combos(); combos.value = r.data || [] } catch { combos.value = [] }
  resetSlideTimer()
})

onUnmounted(() => { if (slideTimer) clearInterval(slideTimer) })
</script>

<style scoped>
/* ====== 轮播图顶部 ====== */
.store-header {
  position: relative;
  background: #1C2536;
}
.banner-track {
  position: relative;
  width: 100%;
  height: 175px;
  overflow: hidden;
  cursor: pointer;
}
.banner-slide {
  position: absolute; inset: 0;
  opacity: 0; transition: opacity 0.7s ease;
  pointer-events: none;
}
.banner-slide.active {
  opacity: 1; pointer-events: auto;
}
.banner-img-wrap { width: 100%; height: 100%; background: #1C2536; }
.banner-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.banner-bg { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
.banner-text { text-align: center; z-index: 1; }
.banner-title { color: #fff; font-size: 22px; font-weight: 600; letter-spacing: 4px; text-shadow: 0 2px 8px rgba(0,0,0,0.2); }
.banner-subtitle { color: rgba(255,255,255,0.6); font-size: 13px; letter-spacing: 3px; margin-top: 8px; }

/* 轮播箭头 */
.banner-arrow {
  position: absolute; top: 50%; transform: translateY(-50%);
  z-index: 5; width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  background: rgba(0,0,0,0.2); border-radius: 50%;
  opacity: 0; transition: opacity 0.3s;
  cursor: pointer;
}
.banner-track:hover .banner-arrow { opacity: 1; }
.banner-arrow-left { left: 6px; }
.banner-arrow-right { right: 6px; }

.banner-dots {
  display: flex; justify-content: center; gap: 5px;
  padding: 6px 0 8px; background: #1C2536;
}
.banner-dots .dot {
  width: 6px; height: 6px; border-radius: 50%; background: rgba(255,255,255,0.25);
  cursor: pointer; transition: all 0.3s;
}
.banner-dots .dot.active { width: 18px; border-radius: 3px; background: #C8925C; }

/* 店铺名浮层 */
.store-overlay {
  position: absolute; top: 0; left: 0; right: 0;
  text-align: center; padding: 14px 16px; z-index: 2;
  background: linear-gradient(180deg, rgba(0,0,0,0.45) 0%, transparent 100%);
  pointer-events: none;
}
.store-title { color: #fff; font-size: 20px; font-weight: 700; letter-spacing: 2px; }
.store-subtitle { color: rgba(255,255,255,0.7); font-size: 11px; margin-top: 2px; }

/* ====== 菜单区 ====== */
.menu-area {
  display: flex;
  background: #f5f3ee;
  min-height: calc(100vh - 255px);
  max-height: calc(100vh - 255px);
  overflow: hidden;
}
.category-side {
  width: 88px;
  background: #fff;
  border-right: 1px solid #eee;
  overflow-y: auto;
  flex-shrink: 0;
}
.cat-item {
  padding: 15px 8px;
  text-align: center;
  font-size: 13px;
  color: #666;
  border-bottom: 1px solid #f5f5f5;
  transition: all .2s;
  position: relative;
  cursor: pointer;
  line-height: 1.3;
  word-break: break-all;
}
.cat-item.active { color: #C8925C; font-weight: 600; background: #FDF6EE; }
.cat-item.active::before {
  content: ''; position: absolute; left: 0; top: 50%;
  transform: translateY(-50%); width: 3px; height: 18px;
  background: #C8925C; border-radius: 0 3px 3px 0;
}
.cat-item.cat-combo { border-bottom: 2px solid #F0E8D8; margin-bottom: 4px; }
.cat-item.cat-combo.active { border-bottom-color: #C8925C; }
.combo-product { border-left: 3px solid #C8925C; background: #FFFCf8; }
.product-list {
  flex: 1;
  padding: 10px;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}
.product-item {
  display: flex; align-items: center; padding: 12px;
  background: #fff; border-radius: 10px; margin-bottom: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  transition: all .2s;
}
.product-item:active { transform: scale(0.98); }
.product-icon {
  width: 44px; height: 44px; border-radius: 10px;
  background: linear-gradient(135deg,#FDF6EE,#F5E6D0);
  display: flex; align-items: center; justify-content: center;
  color: #C8925C; font-weight: 700; font-size: 18px; flex-shrink: 0;
}
.product-img { width: 44px; height: 44px; border-radius: 10px; overflow: hidden; flex-shrink: 0; }
.product-img img { width: 100%; height: 100%; object-fit: cover; }
.product-info { flex: 1; margin-left: 10px; min-width: 0; }
.product-name { font-size: 14px; font-weight: 600; color: #333; }
.product-desc { font-size: 11px; color: #999; margin-top: 1px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.product-price { font-size: 15px; font-weight: 700; color: #C8925C; margin-top: 2px; }
.add-btn { flex-shrink: 0; }

.cart-bar { position:fixed;bottom:50px;left:8px;right:8px;display:flex;align-items:center;background:#1C2536;padding:12px 16px 12px 20px;border-radius:28px;z-index:100;box-shadow:0 4px 20px rgba(0,0,0,0.25); }
.cart-info { flex:1;display:flex;align-items:center;gap:10px;cursor:pointer; }
.cart-icon { position:relative; }
.cart-badge { position:absolute;top:-8px;right:-8px;background:#C8925C;color:#fff;font-size:11px;width:18px;height:18px;border-radius:50%;display:flex;align-items:center;justify-content:center; }
.cart-price { color:#fff;font-size:17px;font-weight:600; }
.cart-btn { background:linear-gradient(135deg,#C8925C,#DFB484);color:#fff;padding:8px 28px;border-radius:20px;font-size:14px;font-weight:600;cursor:pointer;transition:all .2s;box-shadow:0 2px 8px rgba(200,146,92,0.3); }
.cart-btn:active { transform:scale(0.95); }
.pay-summary { display:flex;justify-content:space-between;align-items:center;padding:12px 0 20px;border-bottom:1px solid #f0f0f0;margin-bottom:12px; }

/* 加料选择 */
.addon-grid { display:flex;flex-wrap:wrap;gap:8px; }
.addon-chip {
  display:inline-flex;align-items:center;gap:4px;
  padding:6px 12px;border-radius:16px;border:1px solid #e0e0e0;
  background:#fff;font-size:13px;cursor:pointer;
  transition:all .2s;
}
.addon-chip.selected { border-color:#C8925C;background:#FDF6EE;color:#A6703E;font-weight:500; }
.addon-chip:active { transform:scale(0.95); }

/* 温度/甜度选项 */
.opt-group { display:flex;flex-wrap:wrap;gap:8px; }
.opt-chip {
  display:inline-flex;align-items:center;justify-content:center;
  padding:7px 16px;border-radius:18px;border:1px solid #e0e0e0;
  background:#fff;font-size:13px;cursor:pointer;
  transition:all .2s;min-width:56px;
}
.opt-chip.active { border-color:#C8925C;background:#FDF6EE;color:#A6703E;font-weight:500; }
.opt-chip:active { transform:scale(0.95); }
</style>
