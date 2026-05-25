<template>
  <div class="address-page">
    <van-nav-bar title="地址管理" left-arrow @click-left="$router.back()" />

    <div v-if="loading" style="text-align:center;padding:40px;color:#999">加载中...</div>

    <div v-else-if="list.length === 0" class="empty-state">
      <div style="font-size:48px;margin-bottom:8px">📍</div>
      <p>暂无收货地址</p>
    </div>

    <div v-else class="address-list">
      <div v-for="item in list" :key="item.id" class="address-card">
        <div class="address-top">
          <div class="address-info" @click="selectAddress(item)">
            <div class="address-name">
              {{ item.name }}
              <span class="address-phone">{{ item.phone }}</span>
              <van-tag v-if="item.isDefault" type="warning" size="small" style="margin-left:6px">默认</van-tag>
            </div>
            <div class="address-detail">{{ item.address }}</div>
          </div>
          <van-icon name="edit" size="16" color="#999" @click="openEdit(item)" style="flex-shrink:0;padding:4px" />
        </div>
        <div class="address-actions">
          <div class="addr-action-left" @click="setDefault(item)">
            <van-icon :name="item.isDefault ? 'radio-btn' : 'circle'" :color="item.isDefault ? '#C8925C' : '#ccc'" size="14" />
            <span :style="{ fontSize:'12px', color: item.isDefault ? '#C8925C' : '#999' }">默认地址</span>
          </div>
          <van-icon name="delete" size="16" color="#999" @click="handleDelete(item)" />
        </div>
      </div>
    </div>

    <div style="padding:16px">
      <van-button round block color="#C8925C" @click="openCreate">新增地址</van-button>
    </div>

    <!-- 新增/编辑弹窗 -->
    <van-action-sheet v-model:show="dialog.show" :title="dialog.isEdit ? '编辑地址' : '新增地址'">
      <div style="padding:16px">
        <van-field v-model="dialog.form.name" label="收货人" placeholder="请输入姓名" :rules="[{ required: true }]" />
        <van-field v-model="dialog.form.phone" label="手机号" placeholder="请输入手机号" type="tel" :rules="[{ required: true }]" />
        <van-field v-model="dialog.form.address" label="详细地址" placeholder="街道、门牌号等" type="textarea" :rows="2" :rules="[{ required: true }]" />
        <van-button round block color="#C8925C" style="margin-top:16px" :loading="dialog.saving" @click="save">保存</van-button>
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { addressApi } from '../api'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const dialog = reactive({
  show: false, isEdit: false, saving: false, editId: null,
  form: { name: '', phone: '', address: '' }
})

async function fetchList() {
  loading.value = true
  try { const r = await addressApi.list(); list.value = r.data || [] }
  finally { loading.value = false }
}

function openCreate() {
  dialog.isEdit = false; dialog.editId = null
  dialog.form = { name: '', phone: '', address: '' }; dialog.show = true
}

function openEdit(item) {
  dialog.isEdit = true; dialog.editId = item.id
  dialog.form = { name: item.name, phone: item.phone, address: item.address }; dialog.show = true
}

async function save() {
  if (!dialog.form.name || !dialog.form.phone || !dialog.form.address) return showToast('请填写完整')
  dialog.saving = true
  try {
    if (dialog.isEdit) await addressApi.update(dialog.editId, dialog.form)
    else await addressApi.create(dialog.form)
    showToast('保存成功'); dialog.show = false; fetchList()
  } catch { showToast('保存失败') }
  finally { dialog.saving = false }
}

async function setDefault(item) {
  if (item.isDefault) return
  await addressApi.setDefault(item.id)
  showToast('已设为默认')
  fetchList()
}

function selectAddress(item) {
  // 从订单页跳转过来时把地址存入 sessionStorage
  if (window.__addressCallback) {
    window.__addressCallback = null
  }
  // 把选中的地址存起来，ordering 页面会读取
  sessionStorage.setItem('order_selected_address', JSON.stringify(item))
  router.replace('/ordering')
}

async function handleDelete(item) {
  showConfirmDialog({ message: '确定删除该地址吗？' }).then(async () => {
    await addressApi.delete(item.id); showToast('已删除'); fetchList()
  }).catch(() => {})
}

onMounted(fetchList)
</script>

<style scoped>
.address-page { min-height:100vh;background:#f8f6f0; }
.empty-state { text-align:center;padding:60px 16px;color:#999; }
.address-list { padding:12px 16px; }
.address-card {
  background:#fff;border-radius:10px;padding:14px;margin-bottom:10px;
  box-shadow:0 1px 4px rgba(0,0,0,0.04);
}
.address-top { display:flex;align-items:flex-start;gap:8px; }
.address-info { flex:1;cursor:pointer; }
.address-name { font-size:15px;font-weight:600;color:#333;margin-bottom:4px; }
.address-phone { font-size:13px;font-weight:400;color:#999;margin-left:8px; }
.address-detail { font-size:13px;color:#666;line-height:1.4; }
.address-actions {
  display:flex;justify-content:space-between;align-items:center;
  margin-top:10px;padding-top:8px;border-top:1px solid #f0f0f0;
}
.addr-action-left { display:flex;align-items:center;gap:4px;cursor:pointer; }
</style>
