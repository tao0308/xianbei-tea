<template>
  <div class="orders-page">
    <div class="page-header">
      <h2>我的订单</h2>
      <p>查看您的历史订单和商品清单</p>
    </div>

    <div v-if="loading" style="text-align:center;padding:40px;color:#999">加载中...</div>
    <div v-else-if="list.length === 0" style="text-align:center;padding:40px;color:#999">
      <div style="font-size:48px;margin-bottom:12px">📋</div>
      <p>暂无订单</p>
    </div>

    <div v-else class="order-list">
      <el-card v-for="order in list" :key="order.id" class="order-card">
        <div class="order-head">
          <span class="order-no">取餐号 {{ order.orderNo }}</span>
          <el-tag :type="statusType(order.status)" size="small" effect="dark" round>
            {{ statusText(order.status) }}
          </el-tag>
        </div>

        <div class="order-meta">
          <span>📞 {{ order.customerPhone }}</span>
          <span>🕐 {{ formatTime(order.createdAt) }}</span>
        </div>

        <!-- 商品清单 -->
        <div class="order-items">
          <div v-for="item in (order.items || [])" :key="item.id" class="order-item">
            <span class="item-name">{{ item.productName }}</span>
            <span class="item-qty">x{{ item.quantity }}</span>
            <span class="item-price">¥{{ item.price }}</span>
            <span class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>

        <div class="order-total">
          <span>合计</span>
          <strong>¥{{ order.totalPrice }}</strong>
        </div>

        <div class="order-remark" v-if="order.remark">
          📝 {{ order.remark }}
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { customerApi } from '../api'

const loading = ref(false)
const list = ref([])

function statusType(s) {
  const map = { PENDING: 'info', MAKING: 'warning', DONE: 'success', CANCELLED: 'danger' }
  return map[s] || 'info'
}

function statusText(s) {
  const map = { PENDING: '待处理', MAKING: '制作中', DONE: '已完成', CANCELLED: '已取消' }
  return map[s] || s
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await customerApi.myOrders()
    list.value = res.data || []
  } catch { /* handled */ }
  finally { loading.value = false }
})
</script>

<style scoped>
.orders-page { max-width: 600px; margin: 0 auto; padding: 24px 16px; }
.page-header { margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; }
.page-header p { font-size: 13px; color: #909399; margin-top: 4px; }

.order-card { margin-bottom: 12px; padding: 16px; }
.order-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.order-no { font-size: 16px; font-weight: 700; color: #C8925C; }
.order-meta { display: flex; gap: 16px; font-size: 12px; color: #909399; margin-bottom: 12px; }

.order-items { border-top: 1px solid #f0f0f0; padding-top: 8px; }
.order-item {
  display: flex; align-items: center; gap: 8px;
  padding: 6px 0; font-size: 13px;
}
.item-name { flex: 1; font-weight: 500; }
.item-qty { color: #909399; width: 30px; text-align: center; }
.item-price { color: #909399; width: 50px; text-align: right; }
.item-subtotal { width: 70px; text-align: right; font-weight: 600; color: #303133; }

.order-total {
  display: flex; justify-content: space-between;
  border-top: 1px solid #f0f0f0; padding-top: 10px; margin-top: 4px;
  font-size: 15px;
}
.order-total strong { color: #C8925C; font-size: 18px; }
.order-remark { margin-top: 8px; font-size: 12px; color: #909399; background: #f9f9f9; padding: 6px 10px; border-radius: 6px; }
</style>
