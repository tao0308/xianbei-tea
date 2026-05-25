import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/order-detail/:id', name: 'OrderDetail', component: () => import('../views/OrderDetail.vue'), meta: { auth: true } },
  {
    path: '/',
    component: () => import('../views/MainTab.vue'),
    redirect: '/ordering',
    children: [
      { path: 'ordering', name: 'Ordering', component: () => import('../views/Ordering.vue'), meta: { title: '点单' } },
      { path: 'orders', name: 'Orders', component: () => import('../views/MyOrders.vue'), meta: { title: '订单', auth: true } },
      { path: 'activities', name: 'Activities', component: () => import('../views/Activities.vue'), meta: { title: '活动' } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { title: '我的', auth: true } }
    ]
  },
  { path: '/addresses', name: 'Addresses', component: () => import('../views/Addresses.vue'), meta: { auth: true } }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('customer_token')
  if (to.meta.auth && !token) return next('/login')
  next()
})

export default router
