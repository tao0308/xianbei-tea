<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>员工管理</h2>
          <p>管理所有员工账号，仅店长可见</p>
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">新增员工</el-button>
      </div>

      <el-table :data="userList" border stripe v-loading="loading" style="width: 100%">
        <el-table-column label="头像" width="60" align="center">
          <template #default="{ row }">
            <el-avatar v-if="row.avatarUrl" :src="row.avatarUrl" :size="36" />
            <el-avatar v-else :size="36" style="background:#FDF6EE;color:#C8925C;font-weight:600">
              {{ (row.realName || row.username).charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'MANAGER' ? 'warning' : 'info'" effect="dark" round>
              {{ row.role === 'MANAGER' ? '店长' : '员工' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              @change="handleStatusChange(row)"
              :disabled="row.role === 'MANAGER'"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" @click="openResetPwd(row)" :icon="Key" :disabled="row.role === 'MANAGER'">重置密码</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete" :disabled="row.role === 'MANAGER'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑员工' : '新增员工'" width="500px" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="dialog.form.username" :disabled="dialog.isEdit" placeholder="用于登录" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="dialog.form.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="dialog.form.phone" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!dialog.isEdit">
          <el-input v-model="dialog.form.password" type="password" show-password placeholder="默认密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveUser" :loading="dialog.saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="pwdDialog.visible" title="重置密码" width="400px" :close-on-click-modal="false">
      <el-form :model="pwdDialog.form" ref="pwdFormRef">
        <el-form-item label="新密码" :rules="[{ required: true, message: '请输入新密码' }]">
          <el-input v-model="pwdDialog.form.password" type="password" show-password placeholder="输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="savePassword" :loading="pwdDialog.saving">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Key } from '@element-plus/icons-vue'

const loading = ref(false)
const userList = ref([])
const dialogFormRef = ref(null)
const pwdFormRef = ref(null)

const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { username: '', realName: '', phone: '', password: '' },
  rules: {
    username: [{ required: true, message: '请输入用户名' }],
    realName: [{ required: true, message: '请输入姓名' }],
    phone: [{ required: true, message: '请输入手机号' }]
  }
})

const pwdDialog = reactive({
  visible: false, saving: false, currentId: null,
  form: { password: '' }
})

async function fetchList() {
  loading.value = true
  try { const r = await userApi.list('STAFF'); userList.value = r.data }
  finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false
  dialog.form = { username: '', realName: '', phone: '', password: '' }
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog._editId = row.id
  dialog.form = { username: row.username, realName: row.realName, phone: row.phone, password: '' }
  dialog.visible = true
}

async function saveUser() {
  if (dialog.saving) return
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) {
      await userApi.update(dialog._editId, dialog.form)
      ElMessage.success('更新成功')
    } else {
      await userApi.create(dialog.form)
      ElMessage.success('新增成功')
    }
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function handleStatusChange(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await userApi.updateStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
}

function openResetPwd(row) {
  pwdDialog.currentId = row.id
  pwdDialog.form.password = ''
  pwdDialog.visible = true
}

async function savePassword() {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  pwdDialog.saving = true
  try {
    await userApi.resetPassword(pwdDialog.currentId, pwdDialog.form.password)
    ElMessage.success('密码已重置')
    pwdDialog.visible = false
  } finally { pwdDialog.saving = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除员工「${row.realName}」吗？此操作不可撤销。`, '删除确认', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
  await userApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.page-header h2 { font-size: 18px; font-weight: 600; margin-bottom: 4px; }
.page-header p { font-size: 13px; color: var(--xianbei-text-secondary); }

/* ====== 动作画：交错滑入 ====== */
:deep(.el-table__body-wrapper tbody tr) {
  animation: rowSlideIn 0.4s ease backwards;
}
:deep(.el-table__body-wrapper tbody tr:nth-child(1)) { animation-delay: 0ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(2)) { animation-delay: 60ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(3)) { animation-delay: 120ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(4)) { animation-delay: 180ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(5)) { animation-delay: 240ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(6)) { animation-delay: 300ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(7)) { animation-delay: 360ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(8)) { animation-delay: 420ms; }

@keyframes rowSlideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}

/* ====== 按钮点击波纹 ====== */
.el-button {
  position: relative;
  overflow: hidden;
}

.el-button::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: radial-gradient(circle at center, rgba(255,255,255,0.35) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.25s;
  pointer-events: none;
}

.el-button:active::after {
  opacity: 1;
  transition: opacity 0s;
}

/* ====== 弹窗弹性入场 ====== */
:deep(.el-dialog) {
  animation: dialogBounce 0.35s ease;
}

@keyframes dialogBounce {
  0% { transform: scale(0.9); opacity: 0; }
  60% { transform: scale(1.03); }
  100% { transform: scale(1); opacity: 1; }
}

/* ====== 卡片悬停 ====== */
.page-card {
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.page-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}
</style>
