<template>
  <div class="auth-page">
    <div class="auth-header">
      <div class="auth-logo">🧋</div>
      <h1>注册账号</h1>
      <p>注册后即可在线点单</p>
    </div>
    <van-form @submit="handleRegister">
      <van-cell-group inset>
        <van-field v-model="form.username" label="用户名" placeholder="设置用户名" :rules="[{ required: true, message: '请输入用户名' }]" />
        <van-field v-model="form.realName" label="昵称" placeholder="您的昵称" :rules="[{ required: true, message: '请输入昵称' }]" />
        <van-field v-model="form.phone" label="手机号" placeholder="手机号" type="tel" :rules="[{ required: true, message: '请输入手机号' }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="设置密码" :rules="[{ required: true, message: '请输入密码' }]" />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading" color="#C8925C">注册并登录</van-button>
      </div>
      <div style="text-align: center; font-size: 14px; color: #999">
        已有账号？<router-link to="/login" style="color:#C8925C">去登录</router-link>
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
const form = reactive({ username: '', realName: '', phone: '', password: '' })

async function handleRegister() {
  loading.value = true
  try {
    const res = await customerApi.register(form)
    authStore.setUser(res.data)
    showToast('注册成功')
    router.push('/ordering')
  } catch (e) {
    showToast(e.message || '注册失败')
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
