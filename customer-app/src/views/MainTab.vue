<template>
  <div class="main-tab">
    <router-view />
    <van-tabbar v-model="active" route active-color="#C8925C" inactive-color="#999" border>
      <van-tabbar-item to="/ordering" icon="shop-o">点单</van-tabbar-item>
      <van-tabbar-item to="/orders" icon="records-o" :badge="unreadBadge">订单</van-tabbar-item>
      <van-tabbar-item to="/activities" icon="bullhorn-o">活动</van-tabbar-item>
      <van-tabbar-item to="/profile" icon="contact-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const active = ref(0)
const unreadBadge = ref('')
</script>

<style>
body { margin: 0; background: #f5f3ee; }
/* Vant 主题覆盖 */
:root {
  --van-primary-color: #C8925C;
  --van-tabbar-item-active-color: #C8925C;
  --van-tabs-bottom-bar-color: #C8925C;
  --van-tag-primary-color: #C8925C;
  --van-button-primary-background: #C8925C;
  --van-button-primary-border-color: #C8925C;
  --van-nav-bar-title-text-color: #333;
  --van-cell-group-inset-border-radius: 10px;
}
.main-tab { padding-bottom: 50px; min-height: 100vh; }
</style>
