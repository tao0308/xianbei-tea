<template>
  <div class="fade-in">
    <!-- 库存概览 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <el-card shadow="never" class="summary-card">
          <div class="summary-icon" style="background:#FDF6EE">📦</div>
          <div>
            <div class="summary-value">{{ list.length }}</div>
            <div class="summary-label">原料总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="summary-card">
          <div class="summary-icon" style="background:#FFF3E0">⚠️</div>
          <div>
            <div class="summary-value" style="color:#E6A23C">{{ lowStockCount }}</div>
            <div class="summary-label">库存不足</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="summary-card">
          <div class="summary-icon" style="background:#E8F5E9">✅</div>
          <div>
            <div class="summary-value" style="color:#67C23A">{{ normalStockCount }}</div>
            <div class="summary-label">库存充足</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-card class="page-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="keyword" placeholder="搜索原料..." clearable style="width:200px" @input="fetchList" />
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">新增原料</el-button>
      </div>

      <!-- 库存列表 -->
      <div v-loading="loading" class="stock-list">
        <div v-for="row in filteredList" :key="row.id" class="stock-item" :class="stockClass(row)">
          <div class="stock-left">
            <div class="stock-name">{{ row.name }}</div>
            <div class="stock-status">
              <span class="status-dot" :class="stockDot(row)"></span>
              {{ row.stock <= 0 ? '已耗尽' : row.stock <= row.lowWarning ? '库存不足' : '库存充足' }}
            </div>
          </div>
          <div class="stock-middle">
            <div class="stock-bar-wrap">
              <div class="stock-bar" :style="barStyle(row)">
                <div class="stock-fill" :style="{ width: barWidth(row) + '%', background: barColor(row) }"></div>
              </div>
            </div>
            <div class="stock-nums">
              <span class="stock-current" :style="{ color: row.stock <= row.lowWarning ? '#E6A23C' : '#67C23A' }">
                <strong>{{ row.stock }}</strong> {{ row.unit }}
              </span>
              <span class="stock-warning">预警 ≤ {{ row.lowWarning }}</span>
            </div>
          </div>
          <div class="stock-right">
            <el-button size="small" @click="openStockDialog(row)" :icon="Top" round>入库</el-button>
            <el-button size="small" @click="openEdit(row)" :icon="Edit" round>编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete" round>删除</el-button>
          </div>
        </div>
        <div v-if="filteredList.length === 0 && !loading" class="empty-state">
          <div style="font-size:48px;margin-bottom:8px">📦</div>
          <p>暂无原料数据</p>
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑原料' : '新增原料'" width="480px" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="dialog.form" ref="formRef" label-position="top" label-width="0">
        <el-form-item label="原料名称" prop="name" :rules="[{ required: true, message: '请输入名称' }]">
          <el-input v-model="dialog.form.name" placeholder="如：珍珠、鲜牛奶" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="当前库存" prop="stock">
              <el-input-number v-model="dialog.form.stock" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预警值" prop="lowWarning">
              <el-input-number v-model="dialog.form.lowWarning" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="dialog.form.unit" placeholder="份" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="dialog.saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 入库弹窗 -->
    <el-dialog v-model="stockDialog.visible" title="原料入库" width="380px" :close-on-click-modal="false" destroy-on-close>
      <div class="stock-info-box">
        <span class="stock-info-label">当前库存</span>
        <span class="stock-info-value">{{ stockDialog.currentStock }} {{ stockDialog.unit }}</span>
      </div>
      <el-form :model="stockDialog.form" ref="stockFormRef" label-position="top">
        <el-form-item label="入库数量" prop="amount" :rules="[{ required: true, message: '请输入数量' }, { type: 'number', min: 1, message: '数量至少为 1' }]">
          <el-input-number v-model="stockDialog.form.amount" :min="1" :max="99999" style="width:100%" />
        </el-form-item>
      </el-form>
      <div class="stock-info-box" style="margin-top:8px" v-if="stockDialog.form.amount > 0">
        <span class="stock-info-label">入库后</span>
        <span class="stock-info-value" style="color:#67C23A">{{ stockDialog.currentStock + stockDialog.form.amount }} {{ stockDialog.unit }}</span>
      </div>
      <template #footer>
        <el-button @click="stockDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveStock" :loading="stockDialog.saving">确认入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { inventoryApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Top } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const keyword = ref('')
const formRef = ref(null)
const stockFormRef = ref(null)

const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { name: '', stock: 0, lowWarning: 10, unit: '份' }
})
const stockDialog = reactive({
  visible: false, saving: false, _itemId: null, currentStock: 0, unit: '',
  form: { amount: 1 }
})

const filteredList = computed(() => {
  if (!keyword.value) return list.value
  const kw = keyword.value.toLowerCase()
  return list.value.filter(i => i.name.toLowerCase().includes(kw))
})

const lowStockCount = computed(() => list.value.filter(i => i.stock <= i.lowWarning).length)
const normalStockCount = computed(() => list.value.filter(i => i.stock > i.lowWarning).length)

function stockClass(row) {
  if (row.stock <= 0) return 'stock-depleted'
  if (row.stock <= row.lowWarning) return 'stock-low'
  return ''
}

function stockDot(row) {
  if (row.stock <= 0) return 'dot-red'
  if (row.stock <= row.lowWarning) return 'dot-yellow'
  return 'dot-green'
}

function barWidth(row) {
  // 按预警值的 3 倍作为 100% 参考，超过也算满
  const max = Math.max(row.lowWarning * 3, 1)
  return Math.min(100, (row.stock / max) * 100)
}

function barColor(row) {
  if (row.stock <= 0) return '#F56C6C'
  if (row.stock <= row.lowWarning) return '#E6A23C'
  return '#67C23A'
}

function barStyle(row) {
  const max = Math.max(row.lowWarning * 3, 1)
  const pct = Math.min(100, (row.stock / max) * 100)
  const color = barColor(row)
  return { '--pct': pct + '%', '--color': color }
}

async function fetchList() {
  loading.value = true
  try { const r = await inventoryApi.list(); list.value = r.data }
  finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false; dialog.form = { name: '', stock: 0, lowWarning: 10, unit: '份' }; dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true; dialog._editId = row.id
  dialog.form = { name: row.name, stock: row.stock, lowWarning: row.lowWarning, unit: row.unit }
  dialog.visible = true
}

async function save() {
  if (dialog.saving) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    if (dialog.isEdit) { await inventoryApi.update(dialog._editId, dialog.form) }
    else { await inventoryApi.create(dialog.form) }
    ElMessage.success('保存成功'); dialog.visible = false; fetchList()
  } finally { dialog.saving = false }
}

function openStockDialog(row) {
  stockDialog._itemId = row.id; stockDialog.currentStock = row.stock
  stockDialog.unit = row.unit; stockDialog.form.amount = 1; stockDialog.visible = true
}

async function saveStock() {
  if (stockDialog.form.amount <= 0) return ElMessage.warning('请输入有效数量')
  stockDialog.saving = true
  try {
    await inventoryApi.updateStock(stockDialog._itemId, stockDialog.currentStock + stockDialog.form.amount)
    ElMessage.success(`入库 ${stockDialog.form.amount} 成功`)
    stockDialog.visible = false; fetchList()
  } finally { stockDialog.saving = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除「${row.name}」吗？`, '提示', { type: 'warning' })
  await inventoryApi.delete(row.id); ElMessage.success('已删除'); fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
/* 概览卡片 */
.summary-card {
  display: flex; align-items: center; gap: 14px; padding: 16px 20px;
}
.summary-icon {
  width: 44px; height: 44px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; flex-shrink: 0;
}
.summary-value { font-size: 26px; font-weight: 700; line-height: 1; }
.summary-label { font-size: 12px; color: #909399; margin-top: 4px; }

/* 操作栏 */
.toolbar {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 16px;
}

/* 库存卡片列表 */
.stock-list { min-height: 200px; }
.stock-item {
  display: flex; align-items: center;
  padding: 14px 16px; border-radius: 10px;
  background: #fff; border: 1px solid #f0f0f0;
  margin-bottom: 8px; transition: all 0.2s;
}
.stock-item:hover { border-color: #ddd; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.stock-item.stock-low { border-left: 3px solid #E6A23C; background: #FFFDF5; }
.stock-item.stock-depleted { border-left: 3px solid #F56C6C; background: #FFF5F5; }

.stock-left { width: 150px; flex-shrink: 0; }
.stock-name { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 4px; }
.stock-status { display: flex; align-items: center; gap: 5px; font-size: 12px; color: #909399; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; display: inline-block; }
.dot-green { background: #67C23A; }
.dot-yellow { background: #E6A23C; }
.dot-red { background: #F56C6C; }

.stock-middle { flex: 1; padding: 0 20px; min-width: 0; }
.stock-bar-wrap { margin-bottom: 4px; }
.stock-bar {
  height: 8px; border-radius: 4px; background: #f0f0f0;
  overflow: hidden; position: relative;
}
.stock-fill {
  height: 100%; border-radius: 4px;
  transition: width 0.4s ease;
}
.stock-nums { display: flex; justify-content: space-between; font-size: 12px; }
.stock-current { font-size: 13px; }
.stock-warning { color: #bbb; }

.stock-right {
  display: flex; gap: 6px; flex-shrink: 0;
}

.empty-state { text-align: center; padding: 40px 0; color: #999; }

/* 入库弹窗 */
.stock-info-box {
  display: flex; justify-content: space-between; align-items: center;
  background: #f9f9f9; border-radius: 8px; padding: 10px 14px; margin-bottom: 12px;
}
.stock-info-label { font-size: 13px; color: #909399; }
.stock-info-value { font-size: 16px; font-weight: 600; color: #303133; }

/* 弹窗表单 label 在上方 */
:deep(.el-form--label-top .el-form-item) { margin-bottom: 14px; }
</style>
