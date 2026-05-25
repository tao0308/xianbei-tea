<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="login-bg">
      <!-- 茶叶装饰 -->
      <div class="deco-leaf deco-leaf-1">
        <svg width="140" height="140" viewBox="0 0 140 140" fill="none">
          <path d="M70 120C45 95 30 55 55 30C68 18 82 25 85 28C58 45 60 80 82 100Z" fill="rgba(255,255,255,0.06)" stroke="rgba(255,255,255,0.1)" stroke-width="1.5"/>
          <path d="M85 28C92 22 108 18 112 28C105 32 95 35 85 28Z" fill="rgba(255,255,255,0.06)" stroke="rgba(255,255,255,0.1)" stroke-width="1"/>
        </svg>
      </div>
      <div class="deco-leaf deco-leaf-2">
        <svg width="100" height="120" viewBox="0 0 100 120" fill="none">
          <path d="M50 100C30 75 15 40 38 20C50 10 68 15 72 22C40 35 42 75 65 92Z" fill="rgba(255,255,255,0.05)" stroke="rgba(255,255,255,0.08)" stroke-width="1.5"/>
        </svg>
      </div>
      <!-- 茶杯装饰 -->
      <div class="deco-cup">
        <svg width="80" height="100" viewBox="0 0 80 100" fill="none">
          <path d="M20 40C20 30 28 22 40 22C52 22 60 30 60 40V70C60 80 52 88 40 88C28 88 20 80 20 70V40Z" stroke="rgba(255,255,255,0.07)" stroke-width="1.5" fill="none"/>
          <path d="M60 45C68 45 72 52 70 60C68 66 62 68 60 66" stroke="rgba(255,255,255,0.07)" stroke-width="1.5" fill="none" stroke-linecap="round"/>
          <ellipse cx="40" cy="40" rx="20" ry="5" fill="rgba(255,255,255,0.04)" stroke="rgba(255,255,255,0.07)" stroke-width="1"/>
        </svg>
      </div>
      <!-- 蒸汽粒子 -->
      <div class="steam-particle" v-for="n in 6" :key="n"
        :style="{ left: (10 + n * 15) + '%', animationDelay: (n * 0.6) + 's', animationDuration: (3.5 + n * 0.3) + 's' }">
        <svg width="20" height="40" viewBox="0 0 20 40" fill="none">
          <path d="M10 40C8 30 12 24 10 14" stroke="rgba(255,255,255,0.12)" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
      </div>
    </div>

    <div class="login-container">
      <!-- 左侧品牌展示 -->
      <div class="login-brand">
        <div class="brand-content">
          <div class="brand-logo">
            <svg width="72" height="72" viewBox="0 0 72 72" fill="none">
              <!-- 茶杯光晕 -->
              <circle cx="36" cy="36" r="34" fill="rgba(255,255,255,0.03)"/>
              <!-- 茶杯主体 -->
              <path d="M18 30C18 24 22 18 36 18C50 18 54 24 54 30V48C54 54 50 60 36 60C22 60 18 54 18 48V30Z"
                    fill="url(#cupGrad)" stroke="rgba(255,255,255,0.8)" stroke-width="1.8"/>
              <!-- 杯把 -->
              <path d="M54 32C62 32 64 38 62 44C60 48 56 48 54 46"
                    stroke="rgba(255,255,255,0.7)" stroke-width="2" fill="none" stroke-linecap="round"/>
              <!-- 茶汤 -->
              <ellipse cx="36" cy="30" rx="18" ry="5" fill="rgba(223,180,132,0.5)"/>
              <!-- 茶叶1 (左侧) -->
              <path d="M26 24C22 18 28 12 34 16" stroke="rgba(255,255,255,0.6)" stroke-width="1.8" stroke-linecap="round" fill="none"/>
              <path d="M34 16C38 12 44 14 42 18" stroke="rgba(255,255,255,0.6)" stroke-width="1.8" stroke-linecap="round" fill="none"/>
              <!-- 茶叶2 (右侧) -->
              <path d="M40 20C44 16 50 18 48 22" stroke="rgba(255,255,255,0.5)" stroke-width="1.5" stroke-linecap="round" fill="none"/>
              <!-- 蒸汽 -->
              <path d="M30 16C28 10 32 8 30 4" stroke="rgba(255,255,255,0.3)" stroke-width="1.5" stroke-linecap="round" fill="none">
                <animate attributeName="opacity" values="0.3;0.6;0.3" dur="3s" repeatCount="indefinite"/>
                <animate attributeName="d" values="M30 16C28 10 32 8 30 4;M30 16C32 10 28 8 30 4;M30 16C28 10 32 8 30 4" dur="3s" repeatCount="indefinite"/>
              </path>
              <path d="M38 16C40 12 36 8 38 6" stroke="rgba(255,255,255,0.25)" stroke-width="1.5" stroke-linecap="round" fill="none">
                <animate attributeName="opacity" values="0.25;0.5;0.25" dur="3.5s" repeatCount="indefinite"/>
              </path>
              <path d="M34 14C32 10 36 6 34 3" stroke="rgba(255,255,255,0.2)" stroke-width="1.2" stroke-linecap="round" fill="none">
                <animate attributeName="opacity" values="0.2;0.4;0.2" dur="2.8s" repeatCount="indefinite"/>
              </path>
              <defs>
                <linearGradient id="cupGrad" x1="36" y1="18" x2="36" y2="60">
                  <stop offset="0%" stop-color="rgba(255,255,255,0.15)"/>
                  <stop offset="100%" stop-color="rgba(255,255,255,0.05)"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 class="brand-title">仙贝奶茶店</h1>
          <p class="brand-subtitle">后台管理系统 · 高效运营由此开始</p>
          <div class="brand-features">
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>员工账号管理</span>
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>饮品分类管理</span>
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>菜单上下架</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-wrapper">
        <div class="login-form-card">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的账号</p>

          <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="login-form" @keyup.enter="handleLogin">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                style="width: 100%; height: 44px; font-size: 15px"
                @click="handleLogin"
                :loading="loading"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-links">
            <router-link to="/register">注册账号</router-link>
            <router-link to="/forgot-password">忘记密码</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { authApi } from '../api'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await authApi.login(form)
    authStore.setUser(res.data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #1C2536 0%, #2C3E50 50%, #3D4F5F 100%);
}

/* ====== 茶韵背景装饰 ====== */
.login-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

/* 茶叶 */
.deco-leaf {
  position: absolute;
  opacity: 0.5;
}

.deco-leaf-1 {
  top: 5%;
  left: 3%;
  animation: floatLeaf 10s ease-in-out infinite;
}

.deco-leaf-2 {
  bottom: 10%;
  right: 5%;
  animation: floatLeaf 12s ease-in-out infinite reverse;
}

/* 茶杯 */
.deco-cup {
  position: absolute;
  bottom: 10%;
  left: 12%;
  opacity: 0.4;
  animation: floatLeaf 14s ease-in-out infinite;
  animation-delay: 2s;
}

/* 蒸汽粒子 */
.steam-particle {
  position: absolute;
  bottom: 55%;
  animation: steamRise 4s ease-out infinite;
  pointer-events: none;
}

@keyframes floatLeaf {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-12px) rotate(3deg); }
  50% { transform: translateY(-20px) rotate(-2deg); }
  75% { transform: translateY(-8px) rotate(4deg); }
}

@keyframes steamRise {
  0% { opacity: 0; transform: translateY(0) scale(0.8); }
  20% { opacity: 0.5; }
  60% { opacity: 0.3; transform: translateY(-50px) scale(1.2); }
  100% { opacity: 0; transform: translateY(-90px) scale(1.5); }
}

/* 整体容器 */
.login-container {
  display: flex;
  width: 900px;
  max-width: 90vw;
  min-height: 520px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
}

/* 左侧品牌区 */
.login-brand {
  flex: 1;
  background: linear-gradient(135deg, #1C2536 0%, #2C3E50 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #fff;
}

.brand-content {
  text-align: center;
}

.brand-logo {
  margin-bottom: 20px;
  filter: drop-shadow(0 4px 12px rgba(200, 146, 92, 0.2));
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 8px;
  position: relative;
  display: inline-block;
}

.brand-title::after {
  content: '';
  display: block;
  width: 40px;
  height: 2px;
  background: var(--xianbei-primary);
  margin: 10px auto 0;
  border-radius: 1px;
  opacity: 0.6;
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.7;
  margin-bottom: 32px;
}

.brand-features {
  text-align: left;
  display: inline-block;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  font-size: 14px;
  opacity: 0.85;
}

.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--xianbei-primary);
  flex-shrink: 0;
}

/* 右侧表单区 */
.login-form-wrapper {
  width: 420px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-form-card {
  width: 100%;
}

.form-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--xianbei-text-primary);
  margin-bottom: 4px;
}

.form-subtitle {
  font-size: 14px;
  color: var(--xianbei-text-secondary);
  margin-bottom: 32px;
}

.login-form {
  margin-bottom: 16px;
}

.form-links {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.form-links a {
  color: var(--xianbei-primary);
  text-decoration: none;
  transition: opacity 0.2s;
}

.form-links a:hover {
  opacity: 0.8;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-brand {
    display: none;
  }
  .login-form-wrapper {
    width: 100%;
  }
}
</style>
