<template>
  <div class="profile-page">
    <div class="profile-header">
      <h2>个人信息</h2>
      <p>管理您的账号信息</p>
    </div>

    <el-card class="profile-card" v-loading="loading">
      <div class="avatar-section">
        <el-avatar :size="72" style="background:#FDF6EE;color:#C8925C;font-weight:600;font-size:28px">
          {{ profile.realName?.charAt(0) || profile.username?.charAt(0) || '?' }}
        </el-avatar>
        <div class="avatar-name">{{ profile.realName || profile.username }}</div>
      </div>

      <el-form :model="form" label-width="80px" class="profile-form">
        <el-form-item label="用户名">
          <el-input :model-value="profile.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" placeholder="您的姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="手机号码" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="电子邮箱" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save" :loading="saving" style="width:100%">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { customerApi } from '../api'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const profile = ref({})

const form = reactive({ realName: '', phone: '', email: '' })

async function fetchProfile() {
  loading.value = true
  try {
    const res = await customerApi.getProfile()
    profile.value = res.data
    form.realName = res.data.realName || ''
    form.phone = res.data.phone || ''
    form.email = res.data.email || ''
  } catch { ElMessage.error('获取信息失败') }
  finally { loading.value = false }
}

async function save() {
  saving.value = true
  try {
    await customerApi.updateProfile({
      realName: form.realName,
      phone: form.phone,
      email: form.email
    })
    ElMessage.success('保存成功')
    profile.value.realName = form.realName
  } catch { /* handled by interceptor */ }
  finally { saving.value = false }
}

onMounted(fetchProfile)
</script>

<style scoped>
.profile-page { max-width: 500px; margin: 0 auto; padding: 24px 16px; }
.profile-header { margin-bottom: 20px; }
.profile-header h2 { font-size: 20px; font-weight: 600; }
.profile-header p { font-size: 13px; color: #909399; margin-top: 4px; }
.profile-card { padding: 24px; }
.avatar-section { text-align: center; margin-bottom: 24px; }
.avatar-name { font-size: 16px; font-weight: 600; margin-top: 8px; color: #303133; }
.profile-form { max-width: 360px; margin: 0 auto; }
</style>
