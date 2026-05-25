<template>
  <div class="activities-page">
    <van-nav-bar title="店铺活动" />
    <div v-if="loading"><van-loading style="margin-top:40px" /></div>
    <div v-else-if="posts.length === 0" style="text-align:center;padding:60px 16px;color:#999">暂无活动</div>
    <div v-else style="padding:12px">
      <div v-for="post in posts" :key="post.id" class="post-card">
        <div class="post-title">{{ post.title }}</div>
        <div class="post-content">{{ post.content }}</div>
        <img v-if="post.imageUrl" :src="post.imageUrl" class="post-image" @error="$event.target.style.display='none'" />
        <div class="post-meta">
          <span>{{ formatTime(post.createdAt) }}</span>
          <div class="post-actions">
            <span class="action-btn" @click="toggleLike(post)">
              <van-icon :name="post._liked ? 'like' : 'like-o'" :color="post._liked ? '#C8925C' : '#999'" />
              {{ post.likeCount || 0 }}
            </span>
            <span class="action-btn" @click="showComments(post)">
              <van-icon name="chat-o" /> {{ post.commentCount || 0 }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论弹窗 -->
    <van-action-sheet v-model:show="commentShow" :title="'评论 (' + (currentPost?.commentCount || 0) + ')'">
      <div style="padding:16px;max-height:50vh;overflow-y:auto">
        <!-- 原始评论（parent_id == null） -->
        <div v-for="c in parentComments" :key="c.id" style="margin-bottom:8px">
          <!-- 父评论 -->
          <div style="display:flex;gap:10px;padding:8px 0">
            <div class="comment-avatar">
              <img v-if="c.avatarUrl" :src="c.avatarUrl" class="comment-avatar-img" @error="$event.target.style.display='none'" />
              <span v-else class="comment-avatar-text">{{ c.userName.charAt(0) }}</span>
            </div>
            <div style="flex:1">
              <strong style="font-size:13px">{{ c.userName }}</strong>
              <p style="margin:4px 0;font-size:14px">{{ c.content }}</p>
              <div style="display:flex;gap:12px;font-size:11px;color:#999">
                <span>{{ formatTime(c.createdAt) }}</span>
                <a style="cursor:pointer;color:#C8925C" @click="startReply(c)">回复</a>
              </div>
            </div>
          </div>
          <!-- 子回复（parent_id == c.id） -->
          <div v-for="r in childComments(c.id)" :key="r.id" style="display:flex;gap:10px;padding:10px 0 10px 44px;background:#FAFAFA;border-radius:8px;margin:4px 0">
            <div v-if="r.avatarUrl && r.userName !== '管理员'" style="width:28px;height:28px;flex-shrink:0">
              <img :src="r.avatarUrl" style="width:28px;height:28px;border-radius:50%;object-fit:cover" @error="$event.target.style.display='none'" />
            </div>
            <div v-else style="width:28px;height:28px;border-radius:50%;background:#E8E8E8;color:#666;display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:600;flex-shrink:0">
              {{ r.userName === '管理员' ? '管' : r.userName.charAt(0) }}
            </div>
            <div style="flex:1">
              <strong style="font-size:12px;color:#666">{{ r.userName }}</strong>
              <p style="margin:4px 0;font-size:13px;color:#333">{{ r.content }}</p>
              <div style="display:flex;gap:12px;font-size:11px;color:#999">
                <span>{{ formatTime(r.createdAt) }}</span>
                <a v-if="isLoggedIn" style="cursor:pointer;color:#C8925C" @click="startReply(r)">回复</a>
              </div>
            </div>
          </div>
        </div>
        <div v-if="parentComments.length === 0" style="text-align:center;padding:20px 0;color:#999">暂无评论</div>
        <!-- 回复目标提示 -->
        <div v-if="replyTarget && isLoggedIn" style="font-size:12px;color:#999;margin:8px 0 4px">
          回复 @{{ replyTarget.userName }}：
          <a style="color:#C8925C;margin-left:8px" @click="cancelReply">取消</a>
        </div>
        <van-field v-model="newComment" center :placeholder="replyTarget ? '输入回复…' : '写评论...'" style="margin-top:8px">
          <template #button>
            <van-button size="small" color="#C8925C" @click="submitComment">发送</van-button>
          </template>
        </van-field>
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../store/auth'
import { customerApi } from '../api'
import { showToast } from 'vant'

const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn)

const loading = ref(true)
const posts = ref([])
const commentShow = ref(false)
const currentPost = ref(null)
const comments = ref([])
const newComment = ref('')
const replyTarget = ref(null)

const parentComments = computed(() => comments.value.filter(c => !c.parentId))

function childComments(parentId) {
  return comments.value.filter(c => c.parentId === parentId)
}

function startReply(comment) {
  if (!isLoggedIn.value) return showToast('请先登录')
  replyTarget.value = comment
  newComment.value = ''
}

function cancelReply() {
  replyTarget.value = null
  newComment.value = ''
}

function formatTime(t) { if (!t) return ''; return new Date(t).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) }

async function fetchPosts() {
  try {
    const res = await customerApi.posts()
    posts.value = (res.data || []).map(p => ({ ...p, _liked: false }))
    // 检查已点赞状态
    posts.value.forEach(async (p) => {
      try { const r = await customerApi.checkLike(p.id); p._liked = r.data }
      catch {}
    })
  } finally { loading.value = false }
}

async function toggleLike(post) {
  try {
    const res = await customerApi.toggleLike(post.id)
    post._liked = res.data.liked
    post.likeCount = res.data.count
  } catch { showToast('请先登录') }
}

async function showComments(post) {
  currentPost.value = post; commentShow.value = true; newComment.value = ''
  try { const res = await customerApi.comments(post.id); comments.value = res.data || [] }
  catch { comments.value = [] }
}

async function submitComment() {
  if (!newComment.value) return
  try {
    const parentId = replyTarget.value?.id || null
    await customerApi.addComment(currentPost.value.id, newComment.value, parentId)
    showToast('评论成功')
    newComment.value = ''
    replyTarget.value = null
    const res = await customerApi.comments(currentPost.value.id)
    comments.value = res.data || []
    currentPost.value.commentCount = comments.value.length
  } catch { showToast('请先登录') }
}

onMounted(fetchPosts)
</script>

<style scoped>
.activities-page { min-height:100vh;background:#f5f3ee; }
.comment-avatar { width:32px;height:32px;flex-shrink:0; }
.comment-avatar-img { width:32px;height:32px;border-radius:50%;object-fit:cover; }
.comment-avatar-text { width:32px;height:32px;border-radius:50%;background:#FDF6EE;color:#C8925C;display:flex;align-items:center;justify-content:center;font-size:13px;font-weight:600; }
.post-card { background:#fff;border-radius:14px;padding:18px;margin-bottom:10px;box-shadow:0 2px 8px rgba(0,0,0,0.04); }
.post-title { font-size:16px;font-weight:600;margin-bottom:6px;color:#333; }
.post-content { font-size:14px;color:#666;line-height:1.6; }
.post-image { width:100%;border-radius:10px;margin-top:10px;max-height:200px;object-fit:cover; }
.post-meta { display:flex;justify-content:space-between;align-items:center;margin-top:12px;font-size:12px;color:#999; }
.post-actions { display:flex;gap:16px; }
.action-btn { display:flex;align-items:center;gap:4px;cursor:pointer; }
</style>
