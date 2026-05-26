import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPassword.vue'),
    meta: { title: '忘记密码' }
  },
  {
    path: '/menu',
    name: 'Menu',
    component: () => import('../views/Menu.vue'),
    meta: { title: '在线点单' }
  },
  {
    path: '/customer/profile',
    name: 'CustomerProfile',
    component: () => import('../views/CustomerProfile.vue'),
    meta: { title: '个人信息' }
  },
  {
    path: '/customer/orders',
    name: 'MyOrders',
    component: () => import('../views/MyOrders.vue'),
    meta: { title: '我的订单' }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: { title: '员工管理', role: 'MANAGER' }
      },
      {
        path: 'customers',
        name: 'Customers',
        component: () => import('../views/Customers.vue'),
        meta: { title: '用户管理', role: 'MANAGER' }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('../views/Categories.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/Products.vue'),
        meta: { title: '饮品管理' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'addons',
        name: 'Addons',
        component: () => import('../views/Addons.vue'),
        meta: { title: '加料管理' }
      },
      {
        path: 'posts',
        name: 'Posts',
        component: () => import('../views/Posts.vue'),
        meta: { title: '活动管理' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('../views/Inventory.vue'),
        meta: { title: '库存管理' }
      },
      {
        path: 'banners',
        name: 'Banners',
        component: () => import('../views/Banners.vue'),
        meta: { title: '轮播图管理', role: 'MANAGER' }
      },
      {
        path: 'combos',
        name: 'Combos',
        component: () => import('../views/Combos.vue'),
        meta: { title: '套餐管理', role: 'MANAGER' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 登录校验 + 角色校验
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = (to.meta.title ? `${to.meta.title} · ` : '') + '仙贝奶茶店'

  const token = localStorage.getItem('token')

  // 未登录且不是公开页面 → 跳转登录
  if (!['Login', 'Register', 'ForgotPassword', 'Menu', 'CustomerProfile', 'MyOrders'].includes(to.name) && !token) {
    return next({ name: 'Login' })
  }

  // 已登录但访问登录/注册页 → 跳转工作台
  if (['Login', 'Register', 'ForgotPassword'].includes(to.name) && token) {
    return next({ name: 'Dashboard' })
  }

  // 角色权限校验
  if (to.meta.role && localStorage.getItem('role') !== to.meta.role) {
    ElMessage.error('权限不足')
    return next({ name: 'Dashboard' })
  }

  next()
})

export default router
