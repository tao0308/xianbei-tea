<template>
  <div class="fade-in">
    <!-- KPI 统计卡片 -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background:var(--success-bg);color:var(--success)"><el-icon size="22"><ShoppingCart /></el-icon></div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.todayCount }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background:var(--warning-bg);color:var(--warning)"><el-icon size="22"><Money /></el-icon></div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.todayRevenue }}元</div>
            <div class="stat-label">今日营收</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background:var(--info-bg);color:var(--info)"><el-icon size="22"><Document /></el-icon></div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">累计订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" style="background:var(--danger-bg);color:var(--danger)"><el-icon size="22"><Clock /></el-icon></div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.pendingOrders }}</div>
            <div class="stat-label">待处理</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 + 热销排行 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :xs="24" :md="16">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">近7日营收趋势</span>
              <span class="card-sub">单位：元</span>
            </div>
          </template>
          <div class="bar-chart">
            <div class="bar-item" v-for="day in stats.weeklySales" :key="day.date">
              <div class="bar-top-label">{{ day.revenue }}元</div>
              <div class="bar-track">
                <div class="bar-fill" :style="{ height: barHeight(day.revenue) + '%' }"></div>
              </div>
              <div class="bar-bottom-label">{{ day.date }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="rank-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">热销排行</span>
            </div>
          </template>
          <div class="rank-list" v-if="stats.topProducts && stats.topProducts.length">
            <div class="rank-item" v-for="(item, idx) in stats.topProducts" :key="idx">
              <span class="rank-badge" :class="'rank-' + (idx + 1)">{{ idx + 1 }}</span>
              <span class="rank-name">{{ item.name }}</span>
              <span class="rank-count">{{ item.count }}杯</span>
            </div>
          </div>
          <el-empty v-else description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部交互动画区 -->
    <div class="interact-zone" ref="interactZone"
      @mousemove="onMouseMove" @mousedown.prevent="onMouseDown" @mouseup="onMouseUp" @mouseleave="onMouseLeave">
      <div class="particles-layer">
        <span v-for="i in 8" :key="i" class="particle"
          :style="{
            left: particlePos[i-1]?.x + '%',
            top: particlePos[i-1]?.y + '%',
            transform: 'scale(' + particlePos[i-1]?.s + ')',
            opacity: particlePos[i-1]?.o
          }"></span>
      </div>
      <!-- 掉落食材粒子 -->
      <div class="ingredients-layer" v-if="pressing">
        <span v-for="ing in fallingIngredients" :key="'ing'+ing.id" class="ingredient"
          :style="{ left: ing.x + '%', top: ing.y + '%', transform: 'rotate(' + ing.r + 'deg) scale(' + ing.s + ')', opacity: ing.o, fontSize: ing.size + 'px' }">
          {{ ing.icon }}
        </span>
      </div>
      <div class="tea-cup" :style="cupTransform" :class="{ shaking: pressing }">
        <svg viewBox="0 0 80 80" class="cup-svg">
          <defs>
            <clipPath id="cupBodyClip">
              <rect x="16" y="30" width="48" height="36" rx="6"/>
            </clipPath>
          </defs>
          <!-- 杯身 -->
          <ellipse cx="40" cy="65" rx="28" ry="6" fill="var(--brand-light)" opacity="0.6"/>
          <rect x="16" y="28" width="48" height="38" rx="8" fill="var(--brand)" opacity="0.85"/>
          <!-- 茶液填充 -->
          <g clip-path="url(#cupBodyClip)">
            <rect x="16" :y="64 - teaFillPx" width="48" :height="teaFillPx + 2" fill="#A0522D" opacity="0.55" rx="4"/>
            <rect x="16" :y="68 - teaFillPx * 0.6" width="48" :height="teaFillPx * 0.6 + 2" fill="#D2691E" opacity="0.3" rx="4"/>
            <!-- 泡沫层 -->
            <rect x="16" :y="64 - teaFillPx - 4" width="48" height="6" fill="var(--brand-bg)" opacity="0.5" rx="3"/>
          </g>
          <!-- 杯口 -->
          <ellipse cx="40" cy="28" rx="24" ry="7" fill="var(--brand-light)"/>
          <ellipse cx="40" cy="30" rx="20" ry="4" fill="#7C3A1A" opacity="0.25"/>
          <!-- 珍珠波霸 -->
          <g clip-path="url(#cupBodyClip)">
            <circle v-for="b in bobas" :key="'bo'+b.id"
              :cx="b.cx" :cy="b.cy" r="3"
              fill="#2E1508" :opacity="b.o"/>
          </g>
          <!-- 杯柄 -->
          <path d="M64 32 Q74 32 74 42 Q74 52 64 50" fill="none" stroke="var(--brand)" stroke-width="3" stroke-linecap="round"/>
        </svg>
        <div class="steam" v-for="s in 3" :key="'sm'+s"
          :style="{
            left: (18 + s * 20) + 'px',
            animationDuration: (pressing ? 0.8 + s * 0.3 : 1.5 + s * 0.5) + 's',
            opacity: pressing ? 1 : steamOpacity,
            height: pressing ? '28px' : '18px'
          }"></div>
      </div>
      <p class="interact-hint" :class="{ active: pressing }">
        {{ pressing ? '🥤 奶茶制作中... 松手完成！' : '按住鼠标左键制作奶茶 🍵' }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ShoppingCart, Money, Document, Clock } from '@element-plus/icons-vue'
import request from '@/utils/request'

const stats = reactive({
  todayCount: 0, todayRevenue: '0.00', totalOrders: 0, pendingOrders: 0,
  weeklySales: [], topProducts: []
})

let maxRevenue = 0
function barHeight(revenue) {
  if (!maxRevenue || !revenue) return 0
  return Math.max((revenue / maxRevenue) * 100, 3)
}

async function fetchStats() {
  try {
    const res = await request.get('/dashboard/stats')
    if (res.data) {
      Object.assign(stats, res.data)
      if (stats.weeklySales && stats.weeklySales.length)
        maxRevenue = Math.max(...stats.weeklySales.map(d => Number(d.revenue) || 0))
    }
  } catch (e) { console.error('获取仪表盘数据失败', e) }
}

// ===== 交互动画 =====
const interactZone = ref(null)
const mx = ref(0); const my = ref(0); const hovering = ref(false); const pressing = ref(false)
const particlePos = ref([])
const steamOpacity = ref(0.3)
const teaFillPx = ref(0)
const bobas = ref([])
const fallingIngredients = ref([])
let animId = null
let bobaId = 0
let ingId = 0

const ICONS = ['🫧', '🫘', '🍂', '🌰', '✨', '🟤', '🧋', '🍯']

function initParticles() {
  const arr = []
  for (let i = 0; i < 8; i++) {
    const seed = i * 47
    arr.push({ bx: 30 + Math.sin(seed) * 35, by: 30 + Math.cos(seed * 1.7) * 35, s: 0.7, o: 0.4, p: i * 0.7 })
  }
  particlePos.value = arr
}

function onMouseMove(e) {
  const r = interactZone.value?.getBoundingClientRect()
  if (!r) return
  mx.value = ((e.clientX - r.left) / r.width - 0.5) * 2
  my.value = ((e.clientY - r.top) / r.height - 0.5) * 2
  hovering.value = true
  if (!pressing.value) steamOpacity.value = 0.8
}

function onMouseLeave() {
  hovering.value = false; mx.value = 0; my.value = 0; steamOpacity.value = 0.3
  onMouseUp()
}

function onMouseDown() {
  pressing.value = true
  steamOpacity.value = 1
  teaFillPx.value = 0
  bobas.value = []
  fallingIngredients.value = []
  bobaId = 0; ingId = 0
}

function onMouseUp() {
  if (!pressing.value) return
  pressing.value = false
  steamOpacity.value = hovering.value ? 0.8 : 0.3
}

const cupTransform = computed(() => {
  if (pressing.value) {
    return {
      transform: 'rotate(' + (mx.value * 5).toFixed(1) + 'deg) translateY(' + (my.value * 3).toFixed(1) + 'px) scale(1.12)',
      transition: 'transform 0.08s ease-out'
    }
  }
  if (!hovering.value) return { transform: 'rotate(0deg) translateY(0) scale(1)', transition: 'transform 0.4s ease-out' }
  return {
    transform: 'rotate(' + (mx.value * 8).toFixed(1) + 'deg) translateY(' + (my.value * 5).toFixed(1) + 'px) scale(1.06)',
    transition: 'transform 0.15s ease-out'
  }
})

function animateLoop() {
  const now = Date.now() / 1000

  // 奶茶填充动画
  if (pressing.value) {
    teaFillPx.value = Math.min(teaFillPx.value + 0.5, 30)

    // 生成波霸珍珠
    if (teaFillPx.value > 4 && Math.random() < 0.3) {
      bobas.value.push({
        id: bobaId++, cx: 22 + Math.random() * 34, cy: 64 - teaFillPx.value * 0.3 + Math.random() * 10, o: 0.9
      })
      if (bobas.value.length > 20) bobas.value.shift()
    }

    // 珍珠下沉
    bobas.value.forEach(b => { if (b.cy < 60) b.cy += 0.3 + Math.random() * 0.4 })

    // 掉落食材
    if (Math.random() < 0.15) {
      fallingIngredients.value.push({
        id: ingId++, x: 10 + Math.random() * 80, y: -5, r: Math.random() * 360,
        s: 0.6 + Math.random() * 0.8, o: 1, size: 14 + Math.random() * 10,
        icon: ICONS[Math.floor(Math.random() * ICONS.length)]
      })
      if (fallingIngredients.value.length > 15) fallingIngredients.value.shift()
    }
    fallingIngredients.value.forEach(ing => {
      ing.y += 0.8 + Math.random() * 1.2
      ing.o = ing.y > 80 ? Math.max(0, ing.o - 0.05) : ing.o
      ing.r += 1
    })
    fallingIngredients.value = fallingIngredients.value.filter(ing => ing.o > 0)
  } else {
    teaFillPx.value = Math.max(teaFillPx.value - 1.5, 0)
    if (teaFillPx.value <= 0) { bobas.value = []; fallingIngredients.value = [] }
  }

  // 粒子动画
  const arr = particlePos.value.map((p, i) => {
    let ox, oy
    if (pressing.value) {
      // 点击时：粒子汇聚到杯中
      ox = (50 - p.bx) * 0.6
      oy = (55 - p.by) * 0.6
    } else {
      ox = hovering.value ? mx.value * 35 : 0
      oy = hovering.value ? my.value * 35 : 0
    }
    const float = (hovering.value || pressing.value) ? 0 : Math.sin(now * 1.2 + p.p) * 10
    return {
      x: p.bx + ox, y: p.by + oy + float,
      s: pressing.value ? 0.3 + Math.sin(now * 10 + p.p) * 0.2 : (hovering.value ? 1.5 : 0.6 + Math.sin(now * 1.5 + p.p) * 0.4),
      o: pressing.value ? 0.3 : (hovering.value ? 0.85 : 0.35 + Math.sin(now * 1.3 + p.p) * 0.2)
    }
  })
  particlePos.value = arr
  animId = requestAnimationFrame(animateLoop)
}

onMounted(() => { fetchStats(); initParticles(); animId = requestAnimationFrame(animateLoop) })
onUnmounted(() => { if (animId) cancelAnimationFrame(animId) })
</script>

<style scoped>
.fade-in { animation: fadeIn 0.4s var(--easing); }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }

.kpi-row { margin-bottom: 4px; }

.stat-card {
  border: 1px solid var(--border); border-radius: var(--radius-lg);
  transition: box-shadow var(--duration-fast) var(--easing);
}
.stat-card:hover { box-shadow: var(--shadow-md); }
.stat-card :deep(.el-card__body) {
  display: flex; align-items: center; gap: 14px; padding: 18px 20px;
}
.stat-icon {
  width: 44px; height: 44px; border-radius: var(--radius);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-value { font-size: var(--font-size-2xl); font-weight: 700; color: var(--text-primary); line-height: 1.1; }
.stat-label { font-size: var(--font-size-sm); color: var(--text-muted); margin-top: 2px; }

.chart-card, .rank-card { border: 1px solid var(--border); border-radius: var(--radius-lg); height: 100%; }
.card-header { display: flex; align-items: baseline; gap: 8px; }
.card-title { font-weight: 600; color: var(--text-primary); font-size: var(--font-size-lg); }
.card-sub { font-size: var(--font-size-xs); color: var(--text-muted); }

.bar-chart {
  display: flex; align-items: flex-end; justify-content: space-around;
  height: 200px; padding: 0 8px;
}
.bar-item { display: flex; flex-direction: column; align-items: center; flex: 1; max-width: 60px; }
.bar-top-label {
  font-size: var(--font-size-xs); color: var(--text-muted);
  margin-bottom: 6px; font-weight: 600; white-space: nowrap;
}
.bar-track {
  width: 28px; height: 140px; background: var(--bg-secondary);
  border-radius: 6px 6px 0 0; display: flex; align-items: flex-end; overflow: hidden;
}
.bar-fill {
  width: 100%; background: linear-gradient(180deg, var(--chart-1), var(--brand-light));
  border-radius: 6px 6px 0 0; transition: height 0.6s var(--easing); min-height: 3px;
}
.bar-bottom-label { font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 6px; }

.rank-list { display: flex; flex-direction: column; gap: 8px; }
.rank-item {
  display: flex; align-items: center; gap: 10px; padding: 8px 10px;
  border-radius: var(--radius-sm); background: var(--bg-primary);
  transition: background var(--duration-fast);
}
.rank-item:hover { background: var(--bg-secondary); }
.rank-badge {
  width: 22px; height: 22px; border-radius: 6px;
  display: flex; align-items: center; justify-content: center;
  font-size: var(--font-size-xs); font-weight: 700; color: #fff;
  background: var(--text-muted); flex-shrink: 0;
}
.rank-1 { background: var(--warning); }
.rank-2 { background: var(--brand); }
.rank-3 { background: var(--teal); }
.rank-name { flex: 1; font-size: var(--font-size-base); color: var(--text-primary); }
.rank-count { font-size: var(--font-size-sm); color: var(--text-muted); font-weight: 600; }

/* ===== 底部交互动画区 ===== */
.interact-zone {
  margin-top: 20px;
  height: 200px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--brand-bg) 0%, var(--bg-primary) 50%, var(--teal-bg) 100%);
  border: 1px solid var(--border);
  position: relative;
  overflow: hidden;
  cursor: pointer;
  user-select: none;
}
.particles-layer {
  position: absolute; inset: 0; pointer-events: none; z-index: 1;
}
.particle {
  position: absolute;
  width: 14px; height: 14px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--brand-light) 0%, var(--brand) 60%, transparent 100%);
  filter: blur(1px);
  will-change: left, top, transform, opacity;
}
.particle:nth-child(odd) { background: radial-gradient(circle, var(--teal-light) 0%, var(--teal) 60%, transparent 100%); }
.particle:nth-child(3n) { width: 10px; height: 10px; }
.particle:nth-child(5n) { width: 18px; height: 18px; }

/* 掉落食材 */
.ingredients-layer {
  position: absolute; inset: 0; pointer-events: none; z-index: 3;
  overflow: hidden;
}
.ingredient {
  position: absolute;
  will-change: top, opacity;
  animation: ingredientWobble 0.6s ease-in-out infinite;
}
@keyframes ingredientWobble {
  0%, 100% { margin-left: 0; }
  25% { margin-left: -3px; }
  75% { margin-left: 3px; }
}

.tea-cup {
  position: absolute; left: 50%; top: 50%;
  transform: translate(-50%, -50%);
  z-index: 2; will-change: transform;
}
.cup-svg { width: 80px; height: 80px; filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1)); }

.tea-cup.shaking {
  animation: cupShake 0.15s ease-in-out infinite;
}
@keyframes cupShake {
  0%, 100% { margin-left: 0; }
  25% { margin-left: -3px; }
  75% { margin-left: 3px; }
}

.steam {
  position: absolute; bottom: 56px;
  width: 4px; height: 18px;
  background: rgba(255,255,255,0.5);
  border-radius: 2px;
  animation: steamRise 2s ease-out infinite;
  transition: height 0.3s, opacity 0.3s;
}
.steam:nth-child(2) { animation-delay: 0.4s; }
.steam:nth-child(3) { animation-delay: 0.8s; }

@keyframes steamRise {
  0% { transform: translateY(0) scaleX(1); opacity: 0.6; }
  50% { transform: translateY(-16px) scaleX(2.5); opacity: 0.15; }
  100% { transform: translateY(-32px) scaleX(1); opacity: 0; }
}

.interact-hint {
  position: absolute; bottom: 12px; left: 50%; transform: translateX(-50%);
  font-size: var(--font-size-sm); color: var(--text-muted);
  z-index: 3; pointer-events: none; white-space: nowrap;
  transition: color 0.3s, font-weight 0.3s;
}
.interact-hint.active {
  color: var(--brand-dark);
  font-weight: 600;
}
</style>