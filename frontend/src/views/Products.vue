<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>饮品管理</h2>
          <p>管理所有奶茶饮品的菜单信息</p>
        </div>
        <div style="display: flex; gap: 12px">
          <el-select v-model="filterCategoryId" placeholder="按分类筛选" clearable @change="fetchList" style="width: 160px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
          <el-input v-model="searchKeyword" placeholder="搜索饮品名称…" clearable style="width:200px" @clear="fetchList" @keyup.enter="fetchList">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" @click="openCreate" :icon="Plus">新增饮品</el-button>
        </div>
      </div>

      <el-table :data="list" border stripe v-loading="loading" style="width: 100%">
        <el-table-column label="图片" width="80" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="row.imageUrl"
              style="width:44px;height:44px;border-radius:6px"
              fit="cover"
            >
              <template #error>
                <div style="width:44px;height:44px;border-radius:6px;background: #F8F6F3;display:flex;align-items:center;justify-content:center;font-size:10px;color:#bbb;line-height:1.2;text-align:center">加载<br/>失败</div>
              </template>
            </el-image>
            <div v-else style="width:44px;height:44px;border-radius:6px;background:#FDF6EE;display:flex;align-items:center;justify-content:center;font-size:18px;color:#C8925C">🧋</div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="饮品名称" min-width="140" />
        <el-table-column label="分类" width="120" align="center">
          <template #default="{ row }">
            <el-tag effect="plain" round>{{ getCategoryName(row.categoryId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100" align="center">
          <template #default="{ row }">
            <span style="font-weight: 600; color: #E6A23C">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <div style="display:flex;align-items:center;justify-content:center;gap:4px">
              <el-switch
                :model-value="row.status === 1"
                @change="handleStatusChange(row)"
                size="small"
              />
              <span :style="{ fontSize:'12px', color: row.status === 1 ? '#67C23A' : '#999' }">
                {{ row.status === 1 ? '上架' : '下架' }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑饮品' : '新增饮品'" width="520px" :close-on-click-modal="false">
      <el-form :model="dialog.form" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入饮品名称' }]">
          <el-input v-model="dialog.form.name" placeholder="如：招牌珍珠奶茶" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId" :rules="[{ required: true, message: '请选择分类' }]">
              <el-select v-model="dialog.form.categoryId" placeholder="选择分类" style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price" :rules="[{ required: true, message: '请输入价格' }]">
              <el-input-number v-model="dialog.form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="dialog.form.description" type="textarea" :rows="3" placeholder="饮品的简要描述" />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl">
          <el-input v-model="dialog.form.imageUrl" placeholder="图片URL地址（可选）" />
          <div v-if="dialog.form.imageUrl" style="margin-top:8px">
            <img :src="dialog.form.imageUrl" style="max-width:180px;max-height:100px;border-radius:6px;border: 1px solid #E7E5E4;object-fit:cover" @error="$event.target.style.display='none'" />
          </div>
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
import { productApi, categoryApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const categories = ref([])
const filterCategoryId = ref(null)
const searchKeyword = ref('')
const formRef = ref(null)
const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { name: '', categoryId: null, price: 0, description: '', imageUrl: '' }
})

function getCategoryName(id) { return categories.value.find(c => c.id === id)?.name || '-' }

async function fetchList() {
  loading.value = true
  try { const r = await productApi.list(filterCategoryId.value, searchKeyword.value || null); list.value = r.data }
  finally { loading.value = false }
}

async function fetchCategories() {
  try { const r = await categoryApi.list(); categories.value = r.data } catch {}
}

function openCreate() {
  dialog.isEdit = false
  dialog.form = { name: '', categoryId: null, price: 0, description: '', imageUrl: '' }
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog._editId = row.id
  dialog.form = { name: row.name, categoryId: row.categoryId, price: row.price, description: row.description || '', imageUrl: row.imageUrl || '' }
  dialog.visible = true
}

async function save() {
  if (dialog.saving) return  // 防止重复提交
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) {
      await productApi.update(dialog._editId, dialog.form)
    } else {
      await productApi.create(dialog.form)
    }
    ElMessage.success('保存成功')
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function handleStatusChange(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await productApi.updateStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除饮品「${row.name}」吗？`, '删除确认', { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' })
  await productApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(() => { fetchCategories(); fetchList() })
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
