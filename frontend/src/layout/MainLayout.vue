<template>
  <el-container class="app-container">
    <!-- ====== 左侧边栏 ====== -->
    <el-aside :width="sidebarWidth" class="app-sidebar">
      <!-- 品牌区 -->
      <div class="sidebar-brand">
        <div class="brand-icon">
          <svg width="28" height="28" viewBox="0 0 72 72" fill="none">
            <path d="M18 30C18 24 22 18 36 18C50 18 54 24 54 30V48C54 54 50 60 36 60C22 60 18 54 18 48V30Z" fill="url(#sCup)" stroke="#C8925C" stroke-width="2"/>
            <path d="M54 32C60 32 62 38 60 44C58 48 55 47 54 45" stroke="#C8925C" stroke-width="1.8" fill="none" stroke-linecap="round"/>
            <ellipse cx="36" cy="30" rx="18" ry="4" fill="rgba(223,180,132,0.5)"/>
            <path d="M26 24C23 19 28 14 33 17" stroke="#DFB484" stroke-width="1.5" stroke-linecap="round" fill="none"/>
            <path d="M33 17C36 14 41 15 40 19" stroke="#DFB484" stroke-width="1.5" stroke-linecap="round" fill="none"/>
            <path d="M30 16C29 12 32 10 30 7" stroke="rgba(200,146,92,0.3)" stroke-width="1.2" stroke-linecap="round" fill="none"/>
            <path d="M38 16C39 13 36 11 38 8" stroke="rgba(200,146,92,0.25)" stroke-width="1.2" stroke-linecap="round" fill="none"/>
            <defs><linearGradient id="sCup" x1="36" y1="18" x2="36" y2="60"><stop offset="0%" stop-color="rgba(255,255,255,0.1)"/><stop offset="100%" stop-color="rgba(255,255,255,0.02)"/></linearGradient></defs>
          </svg>
        </div>
        <span class="brand-text" :class="{ collapsed: isCollapsed }">仙贝奶茶店</span>
      </div>

      <!-- 菜单 -->
      <el-menu
        :default-active="route.path"
        :collapse="isCollapsed"
        :background-color="isCollapsed ? 'transparent' : '#1A2332'"
        text-color="#A6B3C9"
        active-text-color="#C8925C"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/users" v-if="authStore.isManager">
          <el-icon><UserFilled /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/customers" v-if="authStore.isManager">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><FolderOpened /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><GoodsFilled /></el-icon>
          <span>饮品管理</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/addons">
          <el-icon><Plus /></el-icon>
          <span>加料管理</span>
        </el-menu-item>
        <el-menu-item index="/posts">
          <el-icon><Collection /></el-icon>
          <span>活动管理</span>
        </el-menu-item>
        <el-menu-item index="/inventory">
          <el-icon><Box /></el-icon>
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/banners" v-if="authStore.isManager">
          <el-icon><PictureFilled /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
        <el-menu-item index="/combos" v-if="authStore.isManager">
          <el-icon><Coin /></el-icon>
          <span>套餐管理</span>
        </el-menu-item>
      </el-menu>

      <!-- 底部折叠按钮 -->
      <div class="sidebar-footer" @click="isCollapsed = !isCollapsed">
        <el-icon :style="{ transition: 'transform 0.3s ease', transform: isCollapsed ? 'rotate(180deg)' : 'none' }">
          <Fold />
        </el-icon>
      </div>
    </el-aside>

    <!-- ====== 右侧内容区 ====== -->
    <el-container class="app-main">
      <!-- 顶部导航栏 -->
      <el-header class="app-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-info">
              <el-avatar :size="32" :style="avatarStyle">
                {{ authStore.realName?.charAt(0) || authStore.username?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ authStore.realName || authStore.username }}</span>
              <el-tag size="small" :type="authStore.isManager ? 'warning' : 'info'" effect="dark" round>
                {{ authStore.isManager ? '店长' : '员工' }}
              </el-tag>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><InfoFilled /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="app-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { ElMessage, ElNotification } from 'element-plus'
import { dashboardApi } from '../api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapsed = ref(false)
const sidebarWidth = computed(() => isCollapsed.value ? '64px' : '240px')

// ====== SSE + 语音播报（仅新订单触发） ======
let eventSource = null
let pollTimer = null
let lastOrderCount = 0

// 获取最自然的语音
function getBestVoice() {
  const voices = window.speechSynthesis?.getVoices() || []
  // 优先神经网络语音（微软/Google 自然人声）
  const natural = voices.find(v => v.lang.startsWith('zh') && (v.name.includes('Neural') || v.name.includes('Natural') || v.name.includes('Xiaoxiao') || v.name.includes('Yunxi') || v.name.includes('Google')))
  if (natural) return natural
  // 其次中文女声
  const female = voices.find(v => v.lang.startsWith('zh') && (v.name.includes('Female') || v.name.includes('girl')))
  if (female) return female
  // 兜底第一个中文语音
  return voices.find(v => v.lang.startsWith('zh'))
}

function speak(text) {
  try {
    window.speechSynthesis?.cancel()
    const msg = new SpeechSynthesisUtterance(text)
    msg.lang = 'zh-CN'
    msg.rate = 0.9
    msg.pitch = 1.1
    msg.volume = 0.9
    const voice = getBestVoice()
    if (voice) msg.voice = voice
    window.speechSynthesis?.speak(msg)
  } catch {}
}

function playAlertSound() {
  speak('您有新的订单，请及时处理')
  setTimeout(() => {
    try {
      const ctx = new (window.AudioContext || window.webkitAudioContext)()
      const o = ctx.createOscillator()
      const g = ctx.createGain()
      o.connect(g); g.connect(ctx.destination)
      o.frequency.value = 880; o.type = 'sine'
      g.gain.setValueAtTime(0.2, ctx.currentTime)
      g.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.3)
      o.start(); o.stop(ctx.currentTime + 0.3)
    } catch {}
  }, 800)
}

function connectSSE() {
  // 预加载语音列表，不播报
  if (window.speechSynthesis) window.speechSynthesis.getVoices()

  try {
    eventSource = new EventSource('/api/sse/orders')
    eventSource.addEventListener('new-order', (e) => {
      playAlertSound()
      ElNotification({
        title: '📢 来新订单了！',
        message: `取餐号 ${e.data}，请尽快处理`,
        type: 'success',
        duration: 8000,
        onClick: () => router.push('/orders')
      })
    })
    eventSource.onerror = () => {
      eventSource?.close()
      startPolling()
    }
  } catch {
    startPolling()
  }
}

function startPolling() {
  if (pollTimer) return
  pollTimer = setInterval(async () => {
    try {
      const res = await dashboardApi.recentOrders(1)
      const orders = res.data || []
      if (orders.length > lastOrderCount && lastOrderCount > 0) {
        playAlertSound()
        ElNotification({
          title: '🔔 新订单提醒', message: `有 ${orders.length - lastOrderCount} 个新订单`,
          type: 'success', duration: 5000
        })
      }
      lastOrderCount = orders.length
    } catch {}
  }, 15000)
}

onMounted(() => {
  connectSSE()
})

onUnmounted(() => {
  if (eventSource) eventSource.close()
  if (pollTimer) clearInterval(pollTimer)
})

const avatarStyle = {
  background: 'linear-gradient(135deg, #C8925C, #DFB484)',
  color: '#fff',
  fontWeight: 600,
  fontSize: '14px',
  border: '2px solid #fff',
  boxShadow: '0 2px 8px rgba(200, 146, 92, 0.3)'
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (cmd === 'profile') {
    ElMessage.info('当前用户：' + authStore.realName)
  }
}
</script>

<style scoped>
/* ====== 布局 ====== */
.app-container {
  height: 100vh;
  overflow: hidden;
  background: var(--xianbei-bg);
}

.app-sidebar {
  background: var(--xianbei-sidebar-bg);
  box-shadow: var(--xianbei-shadow-sidebar);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 100;
  overflow: hidden;
}

/* 品牌区 - 折叠适配 */
.sidebar-brand {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  padding: 0 8px;
}
.sidebar-brand .brand-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.sidebar-brand .brand-text {
  font-size: 17px;
  font-weight: 600;
  color: #FFFFFF;
  letter-spacing: 1px;
  white-space: nowrap;
  transition: opacity 0.25s ease, transform 0.25s ease;
  opacity: 1;
}
.sidebar-brand .brand-text.collapsed {
  opacity: 0;
  transform: translateX(-10px);
  width: 0;
  overflow: hidden;
}

.sidebar-menu {
  flex: 1;
  padding-top: 6px;
  overflow-y: auto;
}

/* 折叠时菜单项居中对齐 */
.el-menu--collapse .el-menu-item {
  margin: 2px 0 !important;
  padding: 0 !important;
  justify-content: center;
  border-radius: 0 !important;
}
.el-menu--collapse .el-menu-item .el-icon {
  margin: 0 !important;
  font-size: 18px;
}

.sidebar-footer {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--xianbei-sidebar-text);
  cursor: pointer;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
  flex-shrink: 0;
}
.sidebar-footer:hover {
  color: #FFFFFF;
  background: rgba(255, 255, 255, 0.05);
}

/* ====== 顶栏 ====== */
.app-header {
  height: 60px !important;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(235, 238, 245, 0.8);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  transition: all 0.2s ease;
}

.user-info:hover {
  background: #F5F7FA;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--xianbei-text-primary);
}

.dropdown-icon {
  color: var(--xianbei-text-secondary);
  font-size: 12px;
}

/* ====== 主内容 ====== */
.app-main {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: var(--xianbei-bg);
}

/* ====== 页面过渡动画 ====== */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(12px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-12px);
}
</style>
