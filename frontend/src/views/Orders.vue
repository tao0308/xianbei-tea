<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>订单管理</h2>
          <p>查看和管理顾客订单</p>
        </div>
        <div style="display:flex;gap:8px">
          <el-button @click="exportOrders" :icon="Download">导出 CSV</el-button>
          <el-input v-model="searchKeyword" placeholder="搜索订单号/顾客名/手机号…" clearable style="width:260px" @clear="fetchList" @keyup.enter="fetchList">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </div>
      </div>

      <el-tabs v-model="activeStatus" @tab-change="fetchList" class="status-tabs">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="已下单" name="PENDING" />
        <el-tab-pane label="制作中" name="MAKING" />
        <el-tab-pane label="已完成" name="DONE" />
        <el-tab-pane label="已取餐" name="PICKED_UP" />
      </el-tabs>

      <div v-loading="loading">
        <div v-if="orders.length === 0" style="text-align:center;padding:60px 0;color:#999">暂无订单</div>
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-no">{{ order.orderNo }}</div>
            <el-tag :type="statusTag(order.status)" effect="dark" round>{{ statusText(order.status) }}</el-tag>
          </div>
          <div class="order-body">
            <div class="order-customer">
              <span>{{ order.customerName }}</span>
              <span class="order-phone">{{ order.customerPhone }}</span>
              <span class="order-time">{{ formatTime(order.createdAt) }}</span>
            </div>
            <div class="order-tags">
              <el-tag size="small" effect="plain">{{ order.orderType === 'PICKUP' ? '自取' : '外卖' }}</el-tag>
              <el-tag size="small" effect="plain" v-if="order.paymentMethod">{{ payText(order.paymentMethod) }}</el-tag>
              <el-tag size="small" effect="plain" v-if="order.address" style="max-width:200px;overflow:hidden;text-overflow:ellipsis">{{ order.address }}</el-tag>
            </div>
            <div class="order-items">
              <div v-for="item in order.items" :key="item.id" class="order-item">
                <span>{{ item.productName }} x{{ item.quantity }}</span>
                <span class="item-notes" v-if="item.tasteNotes">{{ item.tasteNotes }}</span>
                <span class="item-notes" v-if="item.addons">+{{ item.addons }}</span>
              </div>
            </div>
            <div class="order-total">合计：¥{{ order.totalPrice }}</div>
          </div>
          <div class="order-actions" v-if="order.status !== 'PICKED_UP'">
            <el-button v-if="order.status === 'PENDING'" type="warning" @click="updateStatus(order.id, 'MAKING')">开始制作</el-button>
            <el-button v-if="order.status === 'MAKING'" type="success" @click="updateStatus(order.id, 'DONE')">制作完成</el-button>
            <el-button v-if="order.status === 'DONE'" @click="updateStatus(order.id, 'PICKED_UP')">确认取餐</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '../api'
import { ElMessage } from 'element-plus'
import { Search, Download } from '@element-plus/icons-vue'

const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')
const searchKeyword = ref('')

function statusText(s) {
  return { PENDING: '已下单', MAKING: '制作中', DONE: '已完成', PICKED_UP: '已取餐' }[s] || s
}

function payText(s) {
  return { WECHAT: '微信支付', ALIPAY: '支付宝', MOCK: '模拟支付', CASH: '现金' }[s] || s || ''
}

function statusTag(s) {
  return { PENDING: 'danger', MAKING: 'warning', DONE: 'success', PICKED_UP: 'info' }[s] || ''
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

async function fetchList() {
  loading.value = true
  try {
    const res = await orderApi.list(activeStatus.value || null, searchKeyword.value || null)
    orders.value = res.data || []
  } finally { loading.value = false }
}

async function updateStatus(id, status) {
  await orderApi.updateStatus(id, status)
  ElMessage.success(statusText(status))
  fetchList()
}

function exportOrders() {
  const token = localStorage.getItem('token')
  window.open(`/api/orders/export?token=${token}`, '_blank')
}

onMounted(fetchList)
</script>

<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { font-size: 18px; font-weight: 600; margin-bottom: 4px; }
.page-header p { font-size: 13px; color: var(--xianbei-text-secondary); }
.status-tabs { margin-bottom: 16px; }

.order-card {
  background: #fff; border: 1px solid #eee; border-radius: 10px; padding: 16px; margin-bottom: 12px;
  transition: box-shadow 0.2s;
}
.order-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.order-no { font-size: 22px; font-weight: 700; color: #C8925C; letter-spacing: 1px; }
.order-customer { font-size: 15px; font-weight: 500; margin-bottom: 4px; }
.order-phone { font-size: 13px; color: #999; margin-left: 8px; }
.order-time { font-size: 12px; color: #999; margin-left: 12px; }
.order-tags { display:flex;gap:6px;margin:6px 0; }
.order-items { font-size: 14px; margin-bottom: 8px; }
.order-item { margin-bottom: 3px; }
.item-notes { font-size: 12px; color: #999; margin-left: 8px; }
.order-total { font-size: 15px; font-weight: 600; color: #C8925C; }
.order-actions { margin-top: 12px; display: flex; gap: 8px; }
</style>
