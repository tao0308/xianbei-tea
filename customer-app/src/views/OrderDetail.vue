<template>
  <div class="detail-page">
    <van-nav-bar title="订单详情" left-arrow @click-left="$router.back()" />

    <div v-if="loading" style="text-align:center;padding:60px"><van-loading /></div>

    <div v-else-if="order" class="detail-content">
      <!-- 状态和订单号 -->
      <div class="status-section">
        <div class="status-icon">
          <van-icon :name="statusIcon" :color="statusColor" size="40" />
        </div>
        <div class="status-text">{{ statusText(order.status) }}</div>
        <div class="order-no-label">订单号：{{ order.orderNo }}</div>
      </div>

      <!-- 订单信息 -->
      <van-cell-group inset class="info-group">
        <van-cell title="下单时间" :value="formatTime(order.createdAt)" />
        <van-cell title="取餐方式" :value="order.orderType === 'PICKUP' ? '到店自取' : '外卖配送'" />
        <van-cell title="支付方式" :value="payText(order.paymentMethod)" />
        <van-cell title="顾客姓名" :value="order.customerName" />
        <van-cell title="联系电话" :value="order.customerPhone || '-'" />
        <van-cell v-if="order.address" title="送餐地址" :value="order.address" />
        <van-cell v-if="order.remark" title="订单备注" :value="order.remark" />
      </van-cell-group>

      <!-- 商品清单 -->
      <van-cell-group inset class="items-group" style="margin-top:12px">
        <van-cell title="商品清单" />
        <van-cell v-for="item in order.items" :key="item.id">
          <template #title>
            <span>{{ item.productName }} <span style="color:#999;font-size:12px">x{{ item.quantity }}</span></span>
            <span v-if="item.tasteNotes" style="display:block;font-size:12px;color:#999">备注：{{ item.tasteNotes }}</span>
            <span v-if="item.addons" style="display:block;font-size:12px;color:#999">加料：{{ item.addons }}</span>
          </template>
          <template #value>
            <span style="color:#C8925C;font-weight:600">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </template>
        </van-cell>
        <van-cell title="合计">
          <template #value>
            <span style="color:#C8925C;font-size:18px;font-weight:700">¥{{ order.totalPrice }}</span>
          </template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { customerApi } from '../api'

const route = useRoute()
const loading = ref(true)
const order = ref(null)

const statusIcon = computed(() => ({ PENDING: 'clock', MAKING: 'fire', DONE: 'success', PICKED_UP: 'checked' }[order.value?.status] || 'info-o'))
const statusColor = computed(() => ({ PENDING: '#ee0a24', MAKING: '#fa8c16', DONE: '#07c160', PICKED_UP: '#999' }[order.value?.status] || '#999'))

function statusText(s) { return { PENDING: '等待制作', MAKING: '正在制作', DONE: '已完成，请取餐', PICKED_UP: '已取餐' }[s] || s }
function payText(s) { return { WECHAT: '微信支付', ALIPAY: '支付宝', MOCK: '模拟支付', CASH: '现金' }[s] || s || '-' }
function formatTime(t) { if (!t) return ''; return new Date(t).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) }

onMounted(async () => {
  try {
    const res = await customerApi.myOrders()
    const found = (res.data || []).find(o => o.id === Number(route.params.id))
    order.value = found || null
  } finally { loading.value = false }
})
</script>

<style scoped>
.detail-page { min-height:100vh;background:#f8f6f0;padding-bottom:20px; }
.status-section { text-align:center;padding:30px 16px;background:#fff;margin-bottom:12px; }
.status-icon { margin-bottom:8px; }
.status-text { font-size:18px;font-weight:600;color:#333;margin-bottom:4px; }
.order-no-label { font-size:13px;color:#999; }

:deep(.van-cell__title) { flex:2; }
</style>
