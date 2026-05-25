<template>
  <div class="auth-page">
    <div class="auth-header">
      <div class="auth-logo">🧋</div>
      <h1>仙贝奶茶店</h1>
      <p>欢迎回来，请登录</p>
    </div>
    <van-form @submit="handleLogin">
      <van-cell-group inset>
        <van-field v-model="form.username" label="用户名" placeholder="输入用户名" :rules="[{ required: true, message: '请输入用户名' }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="输入密码" :rules="[{ required: true, message: '请输入密码' }]" />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading" color="#C8925C">登录</van-button>
      </div>
      <div style="text-align: center; font-size: 14px; color: #999">
        还没有账号？<router-link to="/register" style="color:#C8925C">去注册</router-link>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { customerApi } from '../api'
import { showToast } from 'vant'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  loading.value = true
  try {
    const res = await customerApi.login(form)
    authStore.setUser(res.data)
    showToast('登录成功')
    router.push('/ordering')
  } catch (e) {
    showToast(e.message || '登录失败')
  } finally { loading.value = false }
}
</script>

<style scoped>
.auth-page { padding: 60px 20px 20px; min-height: 100vh; background: #fff; }
.auth-header { text-align: center; margin-bottom: 32px; }
.auth-logo { font-size: 64px; margin-bottom: 12px; }
.auth-header h1 { font-size: 24px; font-weight: 700; color: #333; }
.auth-header p { font-size: 14px; color: #999; margin-top: 6px; }
</style>
