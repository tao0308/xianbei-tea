<template>
  <div class="fade-in">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>活动管理</h2>
          <p>发布店铺活动信息，顾客端可见并可点赞评论</p>
        </div>
        <el-button type="primary" @click="openCreate" :icon="Plus">发布活动</el-button>
      </div>

      <el-table :data="list" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column label="内容" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">{{ row.content }}</template>
        </el-table-column>
        <el-table-column label="互动" width="160" align="center">
          <template #default="{ row }">❤ {{ row.likeCount }} · 💬 {{ row.commentCount }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160" align="center">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="viewComments(row)">💬 评论</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialog.visible" title="发布活动" width="520px" :close-on-click-modal="false">
      <el-form :model="dialog.form" ref="formRef" label-width="60px">
        <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '请输入标题' }]">
          <el-input v-model="dialog.form.title" placeholder="活动标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content" :rules="[{ required: true, message: '请输入内容' }]">
          <el-input v-model="dialog.form.content" type="textarea" :rows="4" placeholder="活动详细内容" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="dialog.form.imageUrl" placeholder="图片URL地址（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="dialog.saving">发布</el-button>
      </template>
    </el-dialog>

    <!-- 评论查看弹窗 -->
    <el-dialog v-model="commentDialog.visible" :title="'评论 - ' + commentDialog.postTitle" width="520px">
      <div v-if="commentDialog.loading" style="text-align:center;padding:20px">加载中...</div>
      <div v-else style="max-height:400px;overflow-y:auto">
        <div v-if="commentDialog.list.length === 0" style="text-align:center;padding:20px;color:#999">暂无评论</div>
        <!-- 只显示用户原始评论（parent_id == null） -->
        <div v-for="c in userComments" :key="c.id" class="comment-block">
          <div class="comment-row">
            <div class="comment-avatar">
              <img v-if="c.avatarUrl" :src="c.avatarUrl" class="comment-avatar-img" @error="$event.target.style.display='none'" />
              <span v-else class="comment-avatar-txt">{{ c.userName.charAt(0) }}</span>
            </div>
            <div style="flex:1">
              <strong style="font-size:13px">{{ c.userName }}</strong>
              <p style="margin:4px 0;font-size:14px;color:#333">{{ c.content }}</p>
              <div style="display:flex;gap:12px;font-size:12px;color:#999">
                <span>{{ formatTime(c.createdAt) }}</span>
                <a style="cursor:pointer;color:#C8925C" @click="startReply(c)">回复</a>
              </div>
            </div>
          </div>
          <!-- 子回复（parent_id == c.id） -->
          <div v-for="r in commentDialog.list.filter(x => x.parentId === c.id)" :key="r.id" class="reply-row">
            <div v-if="r.avatarUrl && r.userName !== '管理员'" style="width:28px;height:28px;border-radius:50%;overflow:hidden;flex-shrink:0">
              <img :src="r.avatarUrl" style="width:28px;height:28px;object-fit:cover" @error="$event.target.style.display='none'" />
            </div>
            <div v-else class="reply-avatar">{{ r.userName === '管理员' ? '管' : r.userName.charAt(0) }}</div>
            <div style="flex:1">
              <strong style="font-size:12px;color:#666">{{ r.userName }}</strong>
              <p style="margin:4px 0;font-size:13px;color:#333">{{ r.content }}</p>
              <span style="font-size:11px;color:#999">{{ formatTime(r.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 回复输入 -->
      <div style="padding-top:12px;border-top:1px solid #eee;margin-top:8px">
        <div v-if="replyTarget" style="font-size:12px;color:#999;margin-bottom:6px">
          回复 @{{ replyTarget.userName }}：
          <a style="cursor:pointer;color:#C8925C;margin-left:8px" @click="replyTarget=null">取消</a>
        </div>
        <div style="display:flex;gap:8px">
          <el-input v-model="commentInput" :placeholder="replyTarget ? '输入回复…' : '输入管理员回复…'" clearable @keyup.enter="submitComment" />
          <el-button type="primary" @click="submitComment" :loading="commentSaving">{{ replyTarget ? '回复' : '发送' }}</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { postApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const formRef = ref(null)
const dialog = reactive({
  visible: false, saving: false,
  form: { title: '', content: '', imageUrl: '' }
})

const commentDialog = reactive({
  visible: false, loading: false, postTitle: '', list: [], postId: null
})
const commentInput = ref('')
const commentSaving = ref(false)
const replyTarget = ref(null)

const userComments = computed(() =>
  commentDialog.list.filter(c => !c.parentId)
)

async function viewComments(row) {
  commentDialog.postTitle = row.title
  commentDialog.postId = row.id
  commentDialog.list = []
  commentInput.value = ''
  replyTarget.value = null
  commentDialog.visible = true
  commentDialog.loading = true
  try {
    const res = await postApi.comments(row.id)
    commentDialog.list = res.data || []
  } finally { commentDialog.loading = false }
}

function startReply(comment) {
  replyTarget.value = comment
  commentInput.value = ''
}

async function submitComment() {
  if (!commentInput.value.trim()) return
  commentSaving.value = true
  try {
    await postApi.addComment(commentDialog.postId, commentInput.value, replyTarget.value?.id)
    commentInput.value = ''
    replyTarget.value = null
    const res = await postApi.comments(commentDialog.postId)
    commentDialog.list = res.data || []
  } finally { commentSaving.value = false }
}

function formatTime(t) {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

async function fetchList() {
  loading.value = true
  try { const r = await postApi.list(); list.value = r.data }
  finally { loading.value = false }
}

function openCreate() {
  dialog.form = { title: '', content: '', imageUrl: '' }; dialog.visible = true
}

async function save() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  dialog.saving = true
  try {
    await postApi.create(dialog.form)
    ElMessage.success('发布成功'); dialog.visible = false; fetchList()
  } finally { dialog.saving = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除活动「${row.title}」吗？`, '提示', { type: 'warning' })
  await postApi.delete(row.id); ElMessage.success('已删除'); fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-header { display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:20px; }
.page-header h2 { font-size:18px;font-weight:600;margin-bottom:4px; }
.page-header p { font-size:13px;color:var(--xianbei-text-secondary); }
.comment-avatar { width:32px;height:32px;flex-shrink:0; }
.comment-avatar-img { width:32px;height:32px;border-radius:50%;object-fit:cover; }
.comment-avatar-txt { width:32px;height:32px;border-radius:50%;background:#FDF6EE;color:#C8925C;display:flex;align-items:center;justify-content:center;font-size:13px;font-weight:600; }
.comment-block { margin-bottom:12px; }
.comment-row { display:flex;gap:10px;padding:8px 0; }
.reply-row { display:flex;gap:10px;padding:8px 0 8px 42px;background:#FAFAFA;margin:4px 0;border-radius:6px; }
.reply-avatar { width:24px;height:24px;border-radius:50%;background:#E8E8E8;color:#666;display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:600;flex-shrink:0;margin-top:2px; }
</style>
