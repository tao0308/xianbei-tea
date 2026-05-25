<template>
  <div class="page-wrapper">
    <div class="auth-box">
      <div class="auth-header">
        <router-link to="/login" class="back-link">
          <el-icon><ArrowLeft /></el-icon> 返回登录
        </router-link>
        <h2>忘记密码</h2>
        <p>验证用户名和手机号后即可重置密码</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="auth-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="注册时绑定的手机号" size="large" :prefix-icon="Iphone" />
        </el-form-item>
        <el-divider content-position="center">设置新密码</el-divider>
        <el-form-item prop="newPassword">
          <el-input v-model="form.newPassword" type="password" placeholder="新密码" size="large" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%; height: 44px; font-size: 15px"
            @click="handleReset" :loading="loading">重置密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', phone: '', newPassword: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

async function handleReset() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await authApi.forgotPassword(form)
    ElMessage.success(res.data || '密码重置成功')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.auth-box {
  width: 460px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  animation: fadeIn 0.5s ease;
}

.auth-header { margin-bottom: 28px; }

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--xianbei-primary);
  text-decoration: none;
  margin-bottom: 16px;
}

.back-link:hover { opacity: 0.8; }

.auth-header h2 {
  font-size: 22px;
  font-weight: 600;
  color: var(--xianbei-text-primary);
  margin-bottom: 4px;
}

.auth-header p {
  font-size: 13px;
  color: var(--xianbei-text-secondary);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
