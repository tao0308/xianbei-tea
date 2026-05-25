<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>加料管理</h2>
          <p>管理可选的加料选项，顾客点单时可见</p>
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">新增加料</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="加料名称" min-width="160" />
        <el-table-column prop="price" label="价格" width="120" align="center">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑加料' : '新增加料'" width="420px" :close-on-click-modal="false">
      <el-form :model="dialog.form" ref="formRef" label-width="60px">
        <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入加料名称' }]">
          <el-input v-model="dialog.form.name" placeholder="如：珍珠、椰果、布丁" />
        </el-form-item>
        <el-form-item label="价格" prop="price" :rules="[{ required: true, message: '请输入价格' }]">
          <el-input-number v-model="dialog.form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="dialog.saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { addonApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const formRef = ref(null)
const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { name: '', price: 0 }
})

async function fetchList() {
  loading.value = true
  try { const r = await addonApi.list(); list.value = r.data }
  finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false; dialog.form = { name: '', price: 0 }; dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true; dialog._editId = row.id
  dialog.form = { name: row.name, price: row.price }; dialog.visible = true
}

async function save() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) { await addonApi.update(dialog._editId, dialog.form) }
    else { await addonApi.create(dialog.form) }
    ElMessage.success('保存成功')
    dialog.visible = false; fetchList()
  } finally { dialog.saving = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await addonApi.update(row.id, { ...row, status: newStatus })
  row.status = newStatus
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除加料「${row.name}」吗？`, '提示', { type: 'warning' })
  await addonApi.delete(row.id)
  ElMessage.success('已删除'); fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-header {
  display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px;
}
.page-header h2 { font-size: 18px; font-weight: 600; margin-bottom: 4px; }
.page-header p { font-size: 13px; color: var(--xianbei-text-secondary); }
</style>
