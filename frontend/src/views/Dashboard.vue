<template>
  <div class="fade-in">
    <!-- 核心数据卡片 -->
    <el-row :gutter="16">
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background:#E6F7FF;color:#1890FF">📋</div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.todayCount }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background:#F6FFED;color:#52C41A">💰</div>
          <div class="stat-body">
            <div class="stat-value">¥{{ stats.todayRevenue }}</div>
            <div class="stat-label">今日营收</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background:#FFF7E6;color:#FA8C16">⏳</div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.pendingOrders }}</div>
            <div class="stat-label">待处理订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background:#F0F5FF;color:#2F54EB">📦</div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">累计订单</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行：近7日趋势 + 热销排行 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <!-- 近7日趋势 -->
      <el-col :xs="24" :sm="14">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">近7日销售额趋势</span></template>
          <div class="chart-container">
            <div class="bar-chart">
              <div v-for="(day, idx) in stats.weeklySales" :key="idx" class="bar-item">
                <div class="bar-track">
                  <div class="bar-fill" :style="{ height: barHeight(day.revenue) + '%' }"></div>
                </div>
                <div class="bar-val">¥{{ day.revenue }}</div>
                <div class="bar-label">{{ day.date.slice(5) }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 热销排行 -->
      <el-col :xs="24" :sm="10">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">🔥 热销饮品排行</span></template>
          <div v-if="stats.topProducts?.length" class="rank-list">
            <div v-for="(item, idx) in stats.topProducts" :key="idx" class="rank-item">
              <span :class="['rank-num', idx < 3 ? 'top' : '']">{{ idx + 1 }}</span>
              <span class="rank-name">{{ item.name }}</span>
              <span class="rank-count">{{ item.count }}杯</span>
              <div class="rank-bar">
                <div class="rank-fill" :style="{ width: rankWidth(item.count) + '%' }"></div>
              </div>
            </div>
          </div>
          <div v-else style="text-align:center;padding:20px;color:#999">暂无销售数据</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 库存预警 -->
    <el-card v-if="stats.lowStockCount > 0" shadow="never" style="margin-top:16px">
      <template #header><span style="font-weight:600;color:#E6A23C">⚠️ 库存预警</span></template>
      <router-link to="/inventory" style="color:#E6A23C;text-decoration:none;font-size:14px">
        有 {{ stats.lowStockCount }} 种原料库存不足，点击前往补货 →
      </router-link>
    </el-card>

    <!-- 温馨提示 -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header><span style="font-weight:600">💡 快捷入口</span></template>
      <div style="display:flex;gap:12px;flex-wrap:wrap">
        <router-link to="/orders?status=PENDING" class="quick-link">📋 处理新订单</router-link>
        <router-link to="/products" class="quick-link">🧋 管理饮品</router-link>
        <router-link to="/posts" class="quick-link">📢 发布活动</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { dashboardApi, inventoryApi } from '../api'

const stats = reactive({
  todayCount: 0, todayRevenue: '0.00', totalOrders: 0,
  pendingOrders: 0, makingOrders: 0,
  topProducts: [], weeklySales: [],
  lowStockCount: 0
})

let maxRevenue = 0
let maxCount = 0

function barHeight(revenue) {
  if (!maxRevenue || !revenue) return 0
  return Math.max((revenue / maxRevenue) * 100, 3)
}

function rankWidth(count) {
  if (!maxCount || !count) return 0
  return (count / maxCount) * 100
}

async function fetchStats() {
  try {
    const [res, invRes] = await Promise.all([
      dashboardApi.stats(),
      inventoryApi.stats()
    ])
    Object.assign(stats, res.data)
    stats.lowStockCount = invRes.data?.lowCount || 0
    if (stats.weeklySales?.length) {
      maxRevenue = Math.max(...stats.weeklySales.map(d => Number(d.revenue) || 0))
    }
    if (stats.topProducts?.length) {
      maxCount = Math.max(...stats.topProducts.map(d => d.count || 0))
    }
  } catch {}
}

let timer
onMounted(() => {
  fetchStats()
  timer = setInterval(fetchStats, 30000)  // 每30秒刷新
})
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.stat-card { display:flex;align-items:center;gap:16px;padding:8px;border-radius:10px;cursor:default;transition:transform .2s; }
.stat-card:hover { transform:translateY(-2px); }
.stat-icon { width:48px;height:48px;border-radius:10px;display:flex;align-items:center;justify-content:center;font-size:22px;flex-shrink:0; }
.stat-body { flex:1; }
.stat-value { font-size:24px;font-weight:700;line-height:1.2; }
.stat-label { font-size:13px;color:#999;margin-top:2px; }

.chart-container { padding:8px 0; }
.bar-chart { display:flex;align-items:flex-end;gap:8px;height:200px;padding:0 4px; }
.bar-item { flex:1;display:flex;flex-direction:column;align-items:center;height:100%;justify-content:flex-end; }
.bar-track { width:100%;background:#f5f5f5;border-radius:4px 4px 0 0;position:relative;display:flex;align-items:flex-end;flex:1;align-self:stretch; }
.bar-fill { width:100%;background:linear-gradient(180deg,#C8925C,#DFB484);border-radius:4px 4px 0 0;transition:height .6s ease;min-height:3px; }
.bar-val { font-size:10px;color:#C8925C;font-weight:600;margin-top:4px;white-space:nowrap; }
.bar-label { font-size:11px;color:#999;margin-top:2px; }

.rank-list { display:flex;flex-direction:column;gap:8px; }
.rank-item { display:flex;align-items:center;gap:8px;flex-wrap:wrap; }
.rank-num { width:20px;height:20px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:600;background:#f5f5f5;color:#666; }
.rank-num.top { background:#C8925C;color:#fff; }
.rank-name { flex:1;font-size:13px; }
.rank-count { font-size:12px;color:#999;width:40px;text-align:right; }
.rank-bar { width:100%;height:4px;background:#f5f5f5;border-radius:2px; }
.rank-fill { height:4px;background:linear-gradient(90deg,#C8925C,#DFB484);border-radius:2px;transition:width .6s ease; }

.quick-link { padding:10px 20px;border-radius:8px;background:#FDF6EE;color:#C8925C;text-decoration:none;font-size:14px;font-weight:500;transition:all .2s; }
.quick-link:hover { background:#C8925C;color:#fff; }
</style>
