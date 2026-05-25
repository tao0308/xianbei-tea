<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>套餐管理</h2>
          <p>自由搭配饮品和加料组成套餐，自定义定价</p>
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">新增套餐</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading" style="width: 100%">
        <el-table-column label="图片" width="80" align="center">
          <template #default="{ row }">
            <img v-if="row.imageUrl" :src="row.imageUrl" style="width:50px;height:50px;object-fit:cover;border-radius:6px;background:#f5f5f5" />
            <div v-else style="width:50px;height:50px;border-radius:6px;background:#FDF6EE;display:flex;align-items:center;justify-content:center;font-size:18px">🧋</div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="套餐名称" min-width="140" />
        <el-table-column label="内容" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-for="(item, idx) in (row.items || [])" :key="idx" style="margin-right:6px">
              {{ item.productName }} x{{ item.quantity }}<span v-if="idx < row.items.length - 1">、</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="套餐价" width="100" align="center">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.enabled" @change="toggleEnabled(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑套餐' : '新增套餐'" width="620px" :close-on-click-modal="false">
      <el-form :model="dialog.form" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入套餐名称' }]">
          <el-input v-model="dialog.form.name" placeholder="如：超值双人套餐" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="dialog.form.description" type="textarea" :rows="2" placeholder="套餐描述（可选）" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="dialog.form.imageUrl" placeholder="图片 URL（可选）" />
          <div v-if="dialog.form.imageUrl" style="margin-top:6px">
            <img :src="dialog.form.imageUrl" style="max-width:180px;max-height:80px;border-radius:4px;border:1px solid #eee" />
          </div>
        </el-form-item>
        <el-form-item label="套餐价" prop="price" :rules="[{ required: true, message: '请输入价格' }]">
          <el-input-number v-model="dialog.form.price" :min="0" :precision="2" :step="1" style="width:160px" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialog.form.sortOrder" :min="0" :max="999" style="width:120px" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="dialog.form.enabled" />
        </el-form-item>

        <!-- 套餐内容 -->
        <el-divider content-position="left">套餐内容</el-divider>
        <el-form-item label="饮品">
          <div style="width:100%">
            <div v-for="(item, idx) in dialog.items" :key="idx" class="combo-item-row">
              <el-select v-model="item.productId" filterable placeholder="选择饮品" style="width:200px" @change="val => onProductChange(idx, val)">
                <el-option v-for="p in allProducts" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
              <span style="margin:0 6px;color:#999">x</span>
              <el-input-number v-model="item.quantity" :min="1" :max="99" style="width:100px" />
              <el-button size="small" type="danger" text @click="removeItem(idx)" :disabled="dialog.items.length <= 1">
                <el-icon><Delete /></el-icon>
              </el-button>
              <!-- 加料选择 -->
              <div class="addon-tags" v-if="item.productId">
                <el-checkbox-group v-model="item.selectedAddonIds">
                  <el-checkbox v-for="a in allAddons" :key="a.id" :label="a.id" border size="small">{{ a.name }}</el-checkbox>
                </el-checkbox-group>
              </div>
            </div>
            <el-button type="primary" text @click="addItem" :icon="Plus" style="margin-top:6px">添加一款饮品</el-button>
          </div>
        </el-form-item>

        <!-- 预览 -->
        <el-divider content-position="left">预览</el-divider>
        <div class="combo-preview" v-if="dialog.items.length > 0">
          <div class="preview-name">{{ dialog.form.name || '(未命名套餐)' }}</div>
          <div class="preview-items">
            <span v-for="item in previewItems" :key="item.id" class="preview-chip">
              {{ item.label }}
            </span>
          </div>
          <div class="preview-price" v-if="dialog.form.price">¥{{ dialog.form.price }}</div>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="dialog.saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { comboApi, productApi, addonApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const formRef = ref(null)
const allProducts = ref([])
const allAddons = ref([])

const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { name: '', description: '', imageUrl: '', price: 0, sortOrder: 0, enabled: true },
  items: [{ productId: null, quantity: 1, selectedAddonIds: [] }]
})

// 预览：显示套餐包含什么
const previewItems = computed(() =>
  dialog.items.filter(i => i.productId).map(i => {
    const p = allProducts.value.find(x => x.id === i.productId)
    const addonNames = i.selectedAddonIds.map(id => {
      const a = allAddons.value.find(x => x.id === id)
      return a ? a.name : ''
    }).filter(Boolean)
    return {
      id: i.productId,
      label: `${p?.name || '?'} x${i.quantity}${addonNames.length ? ' + ' + addonNames.join('、') : ''}`
    }
  })
)

async function fetchList() {
  loading.value = true
  try { const r = await comboApi.list(); list.value = r.data }
  finally { loading.value = false }
}

async function loadOptions() {
  const [p, a] = await Promise.all([
    productApi.list(),
    addonApi.list()
  ])
  allProducts.value = p.data || []
  allAddons.value = a.data || []
}

function openCreate() {
  dialog.isEdit = false
  dialog._editId = null
  dialog.form = { name: '', description: '', imageUrl: '', price: 0, sortOrder: 0, enabled: true }
  dialog.items = [{ productId: null, quantity: 1, selectedAddonIds: [] }]
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog._editId = row.id
  dialog.form = {
    name: row.name, description: row.description || '', imageUrl: row.imageUrl || '',
    price: row.price, sortOrder: row.sortOrder, enabled: row.enabled
  }
  dialog.items = (row.items || []).map(i => ({
    productId: i.productId,
    quantity: i.quantity,
    selectedAddonIds: i.addonIds ? JSON.parse(i.addonIds) : []
  }))
  if (dialog.items.length === 0) {
    dialog.items = [{ productId: null, quantity: 1, selectedAddonIds: [] }]
  }
  dialog.visible = true
}

function addItem() {
  dialog.items.push({ productId: null, quantity: 1, selectedAddonIds: [] })
}

function removeItem(idx) {
  dialog.items.splice(idx, 1)
}

function onProductChange(idx, val) {
  const product = allProducts.value.find(p => p.id === val)
  if (product) {
    dialog.items[idx]._productName = product.name
  }
}

async function save() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const validItems = dialog.items.filter(i => i.productId)
  if (validItems.length === 0) {
    return ElMessage.warning('请至少选择一款饮品')
  }

  dialog.saving = true
  try {
    const data = {
      ...dialog.form,
      items: validItems.map(i => ({
        productId: i.productId,
        productName: allProducts.value.find(p => p.id === i.productId)?.name || '',
        quantity: i.quantity,
        addonIds: JSON.stringify(i.selectedAddonIds)
      }))
    }
    if (dialog.isEdit) {
      await comboApi.update(dialog._editId, data)
      ElMessage.success('更新成功')
    } else {
      await comboApi.create(data)
      ElMessage.success('新增成功')
    }
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function toggleEnabled(row) {
  const data = {
    name: row.name, description: row.description, price: row.price,
    imageUrl: row.imageUrl, sortOrder: row.sortOrder, enabled: !row.enabled,
    items: (row.items || []).map(i => ({
      productId: i.productId, productName: i.productName,
      quantity: i.quantity, addonIds: i.addonIds || '[]'
    }))
  }
  await comboApi.update(row.id, data)
  row.enabled = !row.enabled
  ElMessage.success(row.enabled ? '已启用' : '已禁用')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除套餐「${row.name}」吗？`, '提示', { type: 'warning' })
  await comboApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(async () => {
  await loadOptions()
  fetchList()
})
</script>

<style scoped>
.page-header { display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:20px; }
.page-header h2 { font-size:18px;font-weight:600;margin-bottom:4px; }
.page-header p { font-size:13px;color:var(--xianbei-text-secondary); }

/* 套餐内容行 */
.combo-item-row {
  background: #FAFAFA; border-radius: 8px; padding: 12px;
  margin-bottom: 8px; display: flex; flex-wrap: wrap; align-items: center; gap: 4px;
}
.addon-tags {
  width: 100%; margin-top: 8px; display: flex; flex-wrap: wrap; gap: 4px;
}
.addon-tags .el-checkbox { margin-right: 0; }

/* 预览 */
.combo-preview {
  background: #FDF6EE; border-radius: 8px; padding: 16px;
  text-align: center;
}
.preview-name { font-size: 16px; font-weight: 600; color: #A6703E; margin-bottom: 8px; }
.preview-items { display: flex; justify-content: center; flex-wrap: wrap; gap: 6px; margin-bottom: 8px; }
.preview-chip {
  background: #fff; border: 1px solid #DFB484; color: #A6703E;
  border-radius: 12px; padding: 3px 10px; font-size: 12px;
}
.preview-price { font-size: 22px; font-weight: 700; color: #C8925C; }
</style>
