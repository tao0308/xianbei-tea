<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>分类管理</h2>
          <p>管理饮品的分类信息</p>
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">新增分类</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="分类名称" min-width="160" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain" round>
              {{ row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑分类' : '新增分类'" width="420px" :close-on-click-modal="false">
      <el-form :model="dialog.form" ref="formRef" label-width="60px">
        <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入分类名称' }]">
          <el-input v-model="dialog.form.name" placeholder="如：招牌奶茶、鲜果茶" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="dialog.form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dialog.form.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
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
import { categoryApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const formRef = ref(null)
const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { name: '', sort: 0, status: 1 }
})

async function fetchList() {
  loading.value = true
  try { const r = await categoryApi.list(); list.value = r.data }
  finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false
  dialog.form = { name: '', sort: 0, status: 1 }
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog._editId = row.id
  dialog.form = { name: row.name, sort: row.sort, status: row.status }
  dialog.visible = true
}

async function save() {
  if (dialog.saving) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) {
      await categoryApi.update(dialog._editId, dialog.form)
    } else {
      await categoryApi.create(dialog.form)
    }
    ElMessage.success('保存成功')
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function handleDelete(row) {
  try {
    await categoryApi.delete(row.id)
    ElMessage.success('已删除')
    fetchList()
  } catch {}
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
</style>
