<template>
  <div class="profile-page">
    <van-nav-bar title="个人中心" />

    <!-- 头像区域 -->
    <div class="profile-header">
      <div class="avatar-wrap" @click="triggerUpload">
        <img v-if="profile.avatarUrl" :src="profile.avatarUrl" class="avatar-img" @error="$event.target.style.display='none'" />
        <div v-else class="avatar-placeholder">{{ (profile.realName || profile.username || '?').charAt(0) }}</div>
        <div class="avatar-badge">
          <van-icon name="photograph" size="14" color="#C8925C" />
        </div>
      </div>
      <div class="profile-info" style="cursor:pointer" @click.stop="showEdit = true">
        <div class="profile-name">{{ profile.realName || profile.username }}</div>
        <div class="profile-role">点击编辑资料</div>
      </div>
    </div>

    <!-- 隐藏的文件上传 -->
    <input ref="fileInputRef" type="file" accept="image/*" style="display:none" @change="onFileSelected" />

    <!-- 菜单 -->
    <van-cell-group inset style="margin:16px">
      <van-cell title="我的订单" is-link to="/orders" icon="records-o" />
      <van-cell title="地址管理" is-link to="/addresses" icon="location-o" />
    </van-cell-group>

    <!-- 退出登录 -->
    <div style="padding:16px">
      <van-button round block @click="handleLogout" color="#ee0a24">退出登录</van-button>
    </div>

    <!-- 编辑资料弹窗 -->
    <van-action-sheet v-model:show="showEdit" title="编辑资料">
      <div style="padding:16px">
        <van-field v-model="editForm.realName" label="昵称" placeholder="输入昵称" />
        <van-field v-model="editForm.phone" label="手机号" placeholder="输入手机号" type="tel" />
        <van-field v-model="editForm.avatarUrl" label="头像URL" placeholder="输入图片链接" />
        <van-button round block color="#C8925C" style="margin-top:16px" :loading="saving" @click="saveProfile">保存</van-button>
      </div>
    </van-action-sheet>

    <!-- ====== 头像裁剪弹窗 ====== -->
    <van-overlay :show="cropper.show" @click="cropper.show = false" />
    <van-transition name="van-slide-up">
      <div v-if="cropper.show" class="cropper-panel">
        <div class="cropper-header">
          <span @click="closeCropper">取消</span>
          <span style="font-weight:600">裁剪头像</span>
          <span style="color:#C8925C;font-weight:600" @click="confirmCrop">确定</span>
        </div>
        <div class="cropper-view">
          <div class="cropper-mask">
            <!-- 裁剪框遮罩 -->
            <svg width="100%" height="100%" class="crop-svg">
              <defs>
                <mask id="cropMask">
                  <rect width="100%" height="100%" fill="white" />
                  <rect :x="cropArea.left" :y="cropArea.top" :width="cropSize" :height="cropSize" fill="black" />
                </mask>
              </defs>
              <rect width="100%" height="100%" fill="rgba(0,0,0,0.5)" mask="url(#cropMask)" />
            </svg>
            <!-- 可拖拽的图片 -->
            <div class="crop-image-wrap"
              @touchstart="onDragStart" @touchmove="onDragMove" @touchend="onDragEnd"
              @mousedown="onDragStart" @mousemove="onDragMove" @mouseup="onDragEnd"
            >
              <img ref="cropImgRef" :src="cropper.imgSrc" class="crop-image"
                :style="cropImageStyle" draggable="false"
                @load="onCropImgLoad" />
            </div>
          </div>
          <!-- 缩放滑块 -->
          <div class="zoom-bar">
            <van-icon name="minus" size="14" color="#999" @click="zoomOut" />
            <input type="range" v-model.number="cropper.scale" min="50" max="200" class="zoom-slider" @input="onZoom" />
            <van-icon name="plus" size="14" color="#999" @click="zoomIn" />
          </div>
          <!-- 预览 -->
          <div class="preview-row">
            <span style="font-size:11px;color:#999">预览</span>
            <canvas ref="previewCanvasRef" width="60" height="60" class="preview-canvas"></canvas>
          </div>
        </div>
      </div>
    </van-transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { customerApi } from '../api'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const authStore = useAuthStore()
const saving = ref(false)
const showEdit = ref(false)
const profile = reactive({ username: '', realName: '', phone: '', avatarUrl: '' })
const editForm = reactive({ realName: '', phone: '', avatarUrl: '' })
const fileInputRef = ref(null)
const cropImgRef = ref(null)
const previewCanvasRef = ref(null)

// 裁剪状态
const cropper = reactive({
  show: false,
  imgSrc: '',
  scale: 100,       // 缩放百分比
  offsetX: 0,       // 图片偏移 X
  offsetY: 0,       // 图片偏移 Y
  dragging: false,
  dragStartX: 0,
  dragStartY: 0,
  dragOrgX: 0,
  dragOrgY: 0,
  naturalW: 0,
  naturalH: 0
})

const CROP_SIZE = 250      // 裁剪框大小（px）
const cropSize = CROP_SIZE
const cropArea = { left: 0, top: 0 }

// 计算图片显示样式
const cropImageStyle = computed(() => {
  const s = cropper.scale / 100
  return {
    transform: `translate(${cropper.offsetX}px, ${cropper.offsetY}px) scale(${s})`,
    cursor: cropper.dragging ? 'grabbing' : 'grab'
  }
})

function triggerUpload() {
  fileInputRef.value?.click()
}

function onFileSelected(e) {
  const file = e.target.files?.[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = (ev) => {
    cropper.imgSrc = ev.target.result
    cropper.scale = 100
    cropper.offsetX = 0
    cropper.offsetY = 0
    cropper.show = true
  }
  reader.readAsDataURL(file)
  // 重置 input 以便重复选同一文件
  fileInputRef.value.value = ''
}

function onCropImgLoad() {
  const img = cropImgRef.value
  if (!img) return
  cropper.naturalW = img.naturalWidth
  cropper.naturalH = img.naturalHeight
  // 计算初始偏移使图片居中
  const view = CROP_SIZE
  const s = cropper.scale / 100
  const dispW = img.naturalWidth * s
  const dispH = img.naturalHeight * s
  cropper.offsetX = (view - dispW) / 2
  cropper.offsetY = (view - dispH) / 2
  updatePreview()
}

function onDragStart(e) {
  cropper.dragging = true
  const pos = getEventPos(e)
  cropper.dragStartX = pos.x
  cropper.dragStartY = pos.y
  cropper.dragOrgX = cropper.offsetX
  cropper.dragOrgY = cropper.offsetY
  e.preventDefault()
}

function onDragMove(e) {
  if (!cropper.dragging) return
  const pos = getEventPos(e)
  const dx = pos.x - cropper.dragStartX
  const dy = pos.y - cropper.dragStartY
  cropper.offsetX = cropper.dragOrgX + dx
  cropper.offsetY = cropper.dragOrgY + dy
  updatePreview()
  e.preventDefault()
}

function onDragEnd() {
  cropper.dragging = false
}

function getEventPos(e) {
  if (e.touches) return { x: e.touches[0].clientX, y: e.touches[0].clientY }
  return { x: e.clientX, y: e.clientY }
}

function zoomIn() {
  cropper.scale = Math.min(200, cropper.scale + 10)
  onZoom()
}

function zoomOut() {
  cropper.scale = Math.max(50, cropper.scale - 10)
  onZoom()
}

function onZoom() {
  // 缩放后保持中心点不变
  updatePreview()
}

function updatePreview() {
  nextTick(() => {
    const img = cropImgRef.value
    const canvas = previewCanvasRef.value
    if (!img || !canvas) return
    const s = cropper.scale / 100
    const size = CROP_SIZE
    const sx = -cropper.offsetX / s
    const sy = -cropper.offsetY / s
    const sw = size / s
    const sh = size / s
    const ctx = canvas.getContext('2d')
    ctx.clearRect(0, 0, 60, 60)
    ctx.drawImage(img, sx, sy, sw, sh, 0, 0, 60, 60)
  })
}

async function confirmCrop() {
  const img = cropImgRef.value
  if (!img) return
  const s = cropper.scale / 100
  const size = CROP_SIZE
  const sx = -cropper.offsetX / s
  const sy = -cropper.offsetY / s
  const sw = size / s
  const sh = size / s

  // 用 canvas 裁剪导出
  const outSize = 200
  const outCanvas = document.createElement('canvas')
  outCanvas.width = outSize
  outCanvas.height = outSize
  const ctx = outCanvas.getContext('2d')
  ctx.drawImage(img, sx, sy, sw, sh, 0, 0, outSize, outSize)

  const dataUrl = outCanvas.toDataURL('image/jpeg', 0.9)
  cropper.show = false

  // 保存头像
  try {
    await customerApi.updateProfile({ avatarUrl: dataUrl })
    profile.avatarUrl = dataUrl
    showToast('头像已更新')
  } catch { showToast('更新失败') }
}

function closeCropper() {
  cropper.show = false
}

async function fetchProfile() {
  try {
    const res = await customerApi.me()
    Object.assign(profile, res.data)
    editForm.realName = profile.realName || ''
    editForm.phone = profile.phone || ''
    editForm.avatarUrl = profile.avatarUrl || ''
  } catch {}
}

async function saveProfile() {
  saving.value = true
  try {
    await customerApi.updateProfile({ realName: editForm.realName, phone: editForm.phone, avatarUrl: editForm.avatarUrl })
    profile.realName = editForm.realName
    profile.phone = editForm.phone
    profile.avatarUrl = editForm.avatarUrl
    showEdit.value = false
    showToast('保存成功')
  } catch { showToast('保存失败') }
  finally { saving.value = false }
}

function handleLogout() {
  showConfirmDialog({ message: '确定退出登录吗？' }).then(() => {
    authStore.logout()
    showToast('已退出')
    router.push('/login')
  }).catch(() => {})
}

onMounted(fetchProfile)
</script>

<style scoped>
.profile-page { min-height:100vh;background:#f8f6f0; }
.profile-header { display:flex;align-items:center;gap:16px;padding:24px 16px;background:#fff; }

.avatar-wrap { position:relative;width:60px;height:60px;cursor:pointer; }
.avatar-img { width:60px;height:60px;border-radius:50%;object-fit:cover; }
.avatar-placeholder { width:60px;height:60px;border-radius:50%;background:#FDF6EE;color:#C8925C;display:flex;align-items:center;justify-content:center;font-size:24px;font-weight:700; }
.avatar-badge {
  position:absolute;bottom:-2px;right:-2px;
  background:#fff;border-radius:50%;padding:3px;
  box-shadow:0 1px 3px rgba(0,0,0,0.15);
  display:flex;
}
.profile-name { font-size:18px;font-weight:600; }
.profile-role { font-size:13px;color:#999;margin-top:4px; }

/* ====== 裁剪面板 ====== */
.cropper-panel {
  position:fixed;bottom:0;left:0;right:0;
  background:#1a1a1a;border-radius:12px 12px 0 0;
  z-index:2001;max-height:85vh;overflow-y:auto;
}
.cropper-header {
  display:flex;justify-content:space-between;align-items:center;
  padding:14px 16px;color:#fff;font-size:15px;
  border-bottom:1px solid rgba(255,255,255,0.08);
}
.cropper-header span:first-child { color:#999;cursor:pointer; }
.cropper-header span:last-child { cursor:pointer; }

.cropper-view { padding:16px;display:flex;flex-direction:column;align-items:center; }

.cropper-mask {
  position:relative;
  width:250px;height:250px;
  overflow:hidden;
  border-radius:8px;
}
.crop-svg { position:absolute;inset:0;z-index:2;pointer-events:none; }
.crop-image-wrap {
  width:100%;height:100%;
  display:flex;align-items:center;justify-content:center;
  overflow:hidden;position:relative;
}
.crop-image { max-width:none;max-height:none;width:auto;height:auto; }

.zoom-bar {
  display:flex;align-items:center;gap:12px;
  margin-top:14px;width:250px;
}
.zoom-slider {
  flex:1;-webkit-appearance:none;appearance:none;
  height:3px;border-radius:2px;background:#444;outline:none;
}
.zoom-slider::-webkit-slider-thumb {
  -webkit-appearance:none;appearance:none;
  width:18px;height:18px;border-radius:50%;
  background:#C8925C;cursor:pointer;border:none;
}
.zoom-bar .van-icon { cursor:pointer;padding:4px; }

.preview-row {
  display:flex;align-items:center;gap:10px;
  margin-top:12px;width:250px;
}
.preview-canvas {
  width:60px;height:60px;border-radius:50%;
  border:2px solid rgba(255,255,255,0.15);
  object-fit:cover;
  margin-left:auto;
}
</style>
