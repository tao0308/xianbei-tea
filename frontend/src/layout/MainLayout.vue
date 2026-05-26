<template>
  <el-container class="app-root">
    <el-aside :width="sidebarWidth" class="app-sidebar">
      <div class="sidebar-brand">
        <div class="brand-icon">
          <svg width="26" height="26" viewBox="0 0 26 26" fill="none">
            <path d="M7 11C7 9 9 7 13 7C17 7 19 9 19 11V17C19 19 17 21 13 21C9 21 7 19 7 17V11Z" stroke="#DFB484" stroke-width="1.5" fill="rgba(200,146,92,0.15)"/>
            <path d="M19 12C21 12 22 14 21 16" stroke="#DFB484" stroke-width="1.2" fill="none" stroke-linecap="round"/>
            <ellipse cx="13" cy="11" rx="6" ry="1.5" fill="rgba(200,146,92,0.3)"/>
          </svg>
        </div>
        <span class="brand-text" :class="{ hide: isCollapsed }">仙贝奶茶</span>
      </div>

      <el-menu :default-active="route.path" :collapse="isCollapsed" router class="sidebar-menu">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
          <el-badge :value="pendingCount" :hidden="!pendingCount" class="menu-badge" />
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><GoodsFilled /></el-icon>
          <span>饮品管理</span>
        </el-menu-item>
        <el-menu-item index="/inventory">
          <el-icon><Box /></el-icon>
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Grid /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/addons">
          <el-icon><Plus /></el-icon>
          <span>加料管理</span>
        </el-menu-item>
        <el-menu-item index="/posts">
          <el-icon><ChatLineSquare /></el-icon>
          <span>活动管理</span>
        </el-menu-item>
        <div class="sidebar-divider"></div>
        <el-menu-item index="/users" v-if="authStore.isManager">
          <el-icon><UserFilled /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/customers" v-if="authStore.isManager">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/banners" v-if="authStore.isManager">
          <el-icon><PictureFilled /></el-icon>
          <span>轮播图</span>
        </el-menu-item>
        <el-menu-item index="/combos" v-if="authStore.isManager">
          <el-icon><Tickets /></el-icon>
          <span>套餐管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer" @click="isCollapsed = !isCollapsed">
        <el-icon :size="16"><Fold /></el-icon>
      </div>
    </el-aside>

    <el-container class="app-main">
      <el-header class="app-header">
        <div class="header-left">
          <el-breadcrumb separator=">">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">仪表盘</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <div class="header-status">
            <span class="status-dot online"></span>
            <span class="status-text">在线</span>
          </div>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-trigger">
              <el-avatar :size="30" class="user-avatar">
                {{ authStore.realName?.charAt(0) || authStore.username?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ authStore.realName || authStore.username }}</span>
              <el-tag size="small" effect="plain" round>
                {{ authStore.isManager ? '管理员' : '店员' }}
              </el-tag>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="app-content">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
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
import {
  DataAnalysis, Document, GoodsFilled, Box, Grid, Plus,
  ChatLineSquare, UserFilled, User, PictureFilled, Tickets,
  Fold, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapsed = ref(false)
const pendingCount = ref(0)
const sidebarWidth = computed(() => isCollapsed.value ? '64px' : '220px')

let eventSource = null
let pollTimer = null

// ====== 语音播报 ======
const voiceProfiles = [
  { keyword: 'Xiaoxiao', pitch: 1.1, rate: 1.0 },
  { keyword: 'Yunxi',    pitch: 0.9, rate: 0.95 },
  { keyword: 'Xiaoyi',   pitch: 1.2, rate: 1.05 },
  { keyword: 'Yunjian',  pitch: 0.85, rate: 1.0 },
]
let voiceIndex = 0

function loadVoices() {
  const voices = window.speechSynthesis?.getVoices()
  if (voices?.length) return
  return new Promise(resolve => {
    window.speechSynthesis?.addEventListener('voiceschanged', resolve, { once: true })
  })
}

function getVoiceByKeyword(keyword) {
  return (window.speechSynthesis?.getVoices() || []).find(
    v => v.lang.startsWith('zh') && v.name.includes(keyword)
  )
}

async function playAlertSound() {
  const profile = voiceProfiles[voiceIndex % voiceProfiles.length]
  voiceIndex++
  try {
    const synth = window.speechSynthesis
    if (!synth) return
    await loadVoices()
    synth.cancel()
    const msg = new SpeechSynthesisUtterance('您有新的订单，请及时处理')
    msg.lang = 'zh-CN'
    msg.pitch = profile.pitch
    msg.rate = profile.rate
    msg.volume = 0.8
    const voice = getVoiceByKeyword(profile.keyword)
    if (voice) msg.voice = voice
    synth.speak(msg)
  } catch {}
}

// ====== SSE / 轮询 ======
function connectSSE() {
  const token = authStore.token
  if (!token) return
  try {
    eventSource = new EventSource('/api/sse/orders?token=' + token)
    eventSource.addEventListener('new-order', (e) => {
      pendingCount.value++
      playAlertSound()
      ElNotification({
        title: '新订单',
        message: '取餐号: ' + e.data,
        type: 'success',
        duration: 6000,
        onClick: () => router.push('/orders')
      })
    })
    eventSource.onerror = () => { eventSource?.close(); startPolling() }
  } catch { startPolling() }
}

function startPolling() {
  if (pollTimer) return
  pollTimer = setInterval(async () => {
    try {
      const res = await dashboardApi.stats()
      const newPending = res.data?.pendingOrders || 0
      if (newPending > pendingCount.value) {
        playAlertSound()
        ElNotification({
          title: '新订单提醒',
          message: '有 ' + (newPending - pendingCount.value) + ' 个新订单',
          type: 'success',
          duration: 5000,
          onClick: () => router.push('/orders')
        })
      }
      pendingCount.value = newPending
    } catch {}
  }, 5000)
}

onMounted(() => { loadVoices(); connectSSE() })
onUnmounted(() => { if (eventSource) eventSource.close(); if (pollTimer) clearInterval(pollTimer) })

function handleCommand(cmd) {
  if (cmd === 'logout') { authStore.logout(); router.push('/login') }
  else if (cmd === 'profile') { ElMessage.info(authStore.realName || authStore.username) }
}
</script>

<style scoped>
.app-root { height: 100vh; overflow: hidden; background: var(--bg-primary); }

.app-sidebar {
  background: var(--bg-sidebar);
  display: flex;
  flex-direction: column;
  transition: width var(--duration) var(--easing);
  z-index: 100;
  overflow: hidden;
}

.sidebar-brand {
  height: var(--header-height);
  display: flex; align-items: center; gap: 10px;
  padding: 0 18px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  flex-shrink: 0;
}
.brand-icon { display: flex; align-items: center; flex-shrink: 0; }
.brand-text {
  font-size: 16px; font-weight: 700; color: #fff;
  letter-spacing: 0.5px; white-space: nowrap;
  transition: opacity var(--duration-fast);
}
.brand-text.hide { opacity: 0; width: 0; overflow: hidden; }

.sidebar-menu { flex: 1; padding-top: 12px; overflow-y: auto; }
.sidebar-divider { height: 1px; margin: 10px 16px; background: rgba(255,255,255,0.06); }

.menu-badge { margin-left: auto; margin-right: 4px; }
.menu-badge :deep(.el-badge__content) {
  position: static; vertical-align: middle; margin-top: 0;
}

.el-menu--collapse .el-menu-item {
  margin: 3px 0 !important; padding: 0 !important;
  justify-content: center; border-radius: 0 !important;
}

.sidebar-footer {
  height: 48px;
  display: flex; align-items: center; justify-content: center;
  color: rgba(255,255,255,0.3); cursor: pointer;
  border-top: 1px solid rgba(255,255,255,0.06);
  flex-shrink: 0;
  transition: color var(--duration-fast);
}
.sidebar-footer:hover { color: rgba(255,255,255,0.6); }

.app-header {
  height: var(--header-height) !important;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border);
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 28px;
  position: sticky; top: 0; z-index: 10;
}

.header-left { display: flex; align-items: center; }
.header-right { display: flex; align-items: center; gap: 16px; }

.header-status {
  display: flex; align-items: center; gap: 6px;
  padding: 5px 12px;
  background: var(--success-bg); border: 1px solid rgba(22,163,74,0.15);
  border-radius: 20px;
}
.status-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--success); }
.status-text { font-size: var(--font-size-xs); color: var(--success); font-weight: 500; }

.user-trigger {
  display: flex; align-items: center; gap: 10px;
  cursor: pointer; padding: 3px 10px 3px 3px; border-radius: 24px;
  transition: background var(--duration-fast);
}
.user-trigger:hover { background: var(--border-light); }
.user-avatar {
  background: linear-gradient(135deg, var(--brand), var(--brand-light)) !important;
  color: #fff !important; font-weight: 700 !important; font-size: 13px !important;
}
.user-name { font-size: var(--font-size-base); font-weight: 600; color: var(--text-primary); }

.app-main { display: flex; flex-direction: column; overflow: hidden; }
.app-content { flex: 1; overflow-y: auto; padding: 28px; }

.page-fade-enter-active,
.page-fade-leave-active { transition: opacity var(--duration-fast) var(--easing); }
.page-fade-enter-from,
.page-fade-leave-to { opacity: 0; }
</style>
