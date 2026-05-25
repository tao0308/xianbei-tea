<template>
  <div class="orders-page">
    <van-nav-bar title="我的订单" />
    <div v-if="loading"><van-loading style="margin-top:40px" /></div>
    <div v-else-if="orders.length === 0" style="text-align:center;padding:60px 16px;color:#999">暂无订单</div>
    <div v-else style="padding:12px">
      <div v-for="order in orders" :key="order.id" class="order-card" @click="goDetail(order.id)">
        <div class="order-header">
          <span class="order-no">{{ order.orderNo }}</span>
          <van-tag :type="statusType(order.status)" round>{{ statusText(order.status) }}</van-tag>
        </div>
        <div class="order-body">
          <div style="font-size:12px;color:#909399;margin-bottom:6px">
            📞 {{ order.customerPhone || order.customerPhone }} · 🕐 {{ formatTime(order.createdAt) }}
          </div>
          <div v-for="item in order.items" :key="item.id" class="order-item">
            {{ item.productName }} x{{ item.quantity }}
            <span style="float:right;color:#C8925C;font-weight:500">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            <span v-if="item.tasteNotes" style="display:block;color:#999;font-size:12px">备注：{{ item.tasteNotes }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span>{{ formatTime(order.createdAt) }}</span>
          <span style="font-weight:700;color:#C8925C">¥{{ order.totalPrice }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { customerApi } from '../api'

const router = useRouter()
const loading = ref(true)
const orders = ref([])

function statusText(s) { return { PENDING: '已下单', MAKING: '制作中', DONE: '已完成', PICKED_UP: '已取餐' }[s] || s }
function statusType(s) { return { PENDING: 'danger', MAKING: 'warning', DONE: 'success', PICKED_UP: 'default' }[s] }
function formatTime(t) { if (!t) return ''; return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) }

function goDetail(id) { router.push(`/order-detail/${id}`) }

onMounted(async () => {
  try { const res = await customerApi.myOrders(); orders.value = res.data || [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.orders-page { min-height:100vh;background:#f8f6f0; }
.order-card { background:#fff;border-radius:10px;padding:14px;margin-bottom:10px;box-shadow:0 1px 4px rgba(0,0,0,0.04);cursor:pointer;transition:transform .15s; }
.order-card:active { transform:scale(0.98); }
.order-header { display:flex;justify-content:space-between;align-items:center;margin-bottom:8px; }
.order-no { font-size:18px;font-weight:700;color:#C8925C; }
.order-body { font-size:14px;margin-bottom:8px; }
.order-item { padding:2px 0; }
.order-footer { display:flex;justify-content:space-between;font-size:12px;color:#999; }
</style>
