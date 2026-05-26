<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>轮播图管理</h2>
          <p>管理顾客点单页顶部轮播图片，仅店长可见</p>
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">新增轮播图</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading" style="width: 100%">
        <el-table-column label="图片" width="120" align="center">
          <template #default="{ row }">
            <img v-if="row.imageUrl" :src="row.imageUrl" style="width:80px;height:45px;object-fit:cover;border-radius:4px;background: #F8F6F3" />
            <el-tag v-else size="small" type="info">无图片</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="linkUrl" label="链接" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.linkUrl" style="color:#409EFF">{{ row.linkUrl }}</span>
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="90" align="center">
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

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑轮播图' : '新增轮播图'" width="540px" :close-on-click-modal="false">
      <el-form :model="dialog.form" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题' }]">
          <el-input v-model="dialog.form.title" placeholder="轮播图标题" />
        </el-form-item>

        <!-- 新增：批量图片 -->
        <el-form-item v-if="!dialog.isEdit" label="图片">
          <el-input
            v-model="dialog.imageUrlsText"
            type="textarea"
            :rows="5"
            placeholder="每行一张图片 URL，一行一个&#10;例如：https://example.com/img1.jpg&#10;https://example.com/img2.jpg"
          />
          <div style="margin-top:8px;font-size:12px;color:#999">
            输入几张图片就会创建几条轮播记录，标题/链接/排序共享
          </div>
          <!-- 预览缩略图 -->
          <div v-if="imagePreviewUrls.length > 0" class="batch-preview">
            <div v-for="(url, idx) in imagePreviewUrls" :key="idx" class="preview-item">
              <img :src="url" @error="$event.target.style.display='none'" />
              <span class="preview-num">#{{ idx + 1 }}</span>
            </div>
          </div>
        </el-form-item>

        <!-- 编辑：单张图片 -->
        <el-form-item v-else label="图片URL">
          <el-input v-model="dialog.form.imageUrl" placeholder="图片地址" />
          <div v-if="dialog.form.imageUrl" style="margin-top:8px">
            <img :src="dialog.form.imageUrl" style="max-width:200px;max-height:100px;border-radius:4px;border: 1px solid #E7E5E4" />
          </div>
        </el-form-item>

        <el-form-item label="链接">
          <el-input v-model="dialog.form.linkUrl" placeholder="点击跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialog.form.sortOrder" :min="0" :max="999" />
          <span style="margin-left:8px;font-size:12px;color:#999">批量添加时每张图自动 +1</span>
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="dialog.form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="dialog.saving">
          {{ dialog.isEdit ? '保存' : `批量保存（${imageUrlCount}张）` }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { bannerApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const formRef = ref(null)

const dialog = reactive({
  visible: false, isEdit: false, saving: false, _editId: null,
  form: { title: '', imageUrl: '', linkUrl: '', sortOrder: 0, enabled: true },
  imageUrlsText: ''
})

// 从 textarea 解析出的图片 URL 列表
const imageUrls = computed(() =>
  dialog.imageUrlsText.split('\n').map(s => s.trim()).filter(s => s.length > 0)
)

// 预览缩略图（最多显示8张）
const imagePreviewUrls = computed(() => imageUrls.value.slice(0, 8))

// 按钮上显示数量
const imageUrlCount = computed(() => imageUrls.value.length)

async function fetchList() {
  loading.value = true
  try { const r = await bannerApi.list(); list.value = r.data }
  finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false
  dialog._editId = null
  dialog.form = { title: '', imageUrl: '', linkUrl: '', sortOrder: 0, enabled: true }
  dialog.imageUrlsText = ''
  dialog.visible = true
}

function openEdit(row) {
  dialog.isEdit = true
  dialog._editId = row.id
  dialog.form = { title: row.title, imageUrl: row.imageUrl || '', linkUrl: row.linkUrl || '', sortOrder: row.sortOrder, enabled: row.enabled }
  dialog.imageUrlsText = ''
  dialog.visible = true
}

async function save() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  dialog.saving = true
  try {
    if (dialog.isEdit) {
      await bannerApi.update(dialog._editId, dialog.form)
      ElMessage.success('更新成功')
    } else {
      const urls = imageUrls.value
      if (urls.length === 0) {
        ElMessage.warning('请至少输入一张图片 URL')
        dialog.saving = false
        return
      }
      // 批量创建：每条记录共享标题/链接，排序自动递增
      const promises = urls.map((url, idx) =>
        bannerApi.create({
          title: dialog.form.title,
          imageUrl: url,
          linkUrl: dialog.form.linkUrl,
          sortOrder: dialog.form.sortOrder + idx,
          enabled: dialog.form.enabled
        })
      )
      await Promise.all(promises)
      ElMessage.success(`成功新增 ${urls.length} 张轮播图`)
    }
    dialog.visible = false
    fetchList()
  } finally { dialog.saving = false }
}

async function toggleEnabled(row) {
  await bannerApi.update(row.id, { ...row, enabled: !row.enabled })
  row.enabled = !row.enabled
  ElMessage.success(row.enabled ? '已启用' : '已禁用')
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除轮播图「${row.title}」吗？`, '提示', { type: 'warning' })
  await bannerApi.delete(row.id)
  ElMessage.success('已删除')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-header { display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:20px; }
.page-header h2 { font-size:18px;font-weight:600;margin-bottom:4px; }
.page-header p { font-size:13px;color:var(--xianbei-text-secondary); }

/* 批量图片预览 */
.batch-preview { display:flex;flex-wrap:wrap;gap:8px;margin-top:10px; }
.preview-item { position:relative;width:64px;height:64px;border-radius:6px;overflow:hidden;border: 1px solid #E7E5E4; }
.preview-item img { width:100%;height:100%;object-fit:cover; }
.preview-num {
  position:absolute;top:2px;right:2px;
  background:rgba(0,0,0,0.55);color:#fff;
  font-size:10px;padding:1px 5px;border-radius:4px;
}

/* 动画继承 */
:deep(.el-table__body-wrapper tbody tr) {
  animation: rowSlideIn 0.4s ease backwards;
}
:deep(.el-table__body-wrapper tbody tr:nth-child(1)) { animation-delay: 0ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(2)) { animation-delay: 60ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(3)) { animation-delay: 120ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(4)) { animation-delay: 180ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(5)) { animation-delay: 240ms; }
:deep(.el-table__body-wrapper tbody tr:nth-child(6)) { animation-delay: 300ms; }

@keyframes rowSlideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}
</style>
