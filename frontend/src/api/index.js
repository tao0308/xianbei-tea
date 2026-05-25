import request from '../utils/request'

// ==================== 认证模块 ====================
export const authApi = {
  login: data => request.post('/auth/login', data),
  register: data => request.post('/auth/register', data),
  forgotPassword: data => request.post('/auth/forgot-password', data),
  resetPassword: data => request.post('/auth/reset-password', data)
}

// ==================== 用户/员工管理 ====================
export const userApi = {
  list: (role) => request.get('/users', { params: { role } }),
  create: data => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  updateStatus: (id, status) => request.put(`/users/${id}/status`, { status }),
  resetPassword: (id, password) => request.put(`/users/${id}/password`, { password }),
  delete: id => request.delete(`/users/${id}`)
}

// ==================== 分类管理 ====================
export const categoryApi = {
  list: () => request.get('/categories'),
  create: data => request.post('/categories', data),
  update: (id, data) => request.put(`/categories/${id}`, data),
  delete: id => request.delete(`/categories/${id}`)
}

// ==================== 饮品管理 ====================
export const productApi = {
  list: (categoryId, keyword) => request.get('/products', { params: { categoryId, keyword } }),
  create: data => request.post('/products', data),
  update: (id, data) => request.put(`/products/${id}`, data),
  updateStatus: (id, status) => request.put(`/products/${id}/status`, { status }),
  delete: id => request.delete(`/products/${id}`)
}

// ==================== 顾客点单（公开） ====================
export const publicApi = {
  categories: () => request.get('/public/categories'),
  products: (categoryId) => request.get('/public/products', { params: { categoryId } }),
  addons: () => request.get('/public/addons'),
  createOrder: data => request.post('/public/orders', data),
  banners: () => request.get('/public/banners'),
  combos: () => request.get('/public/combos')
}

// ==================== 后台订单管理 ====================
export const orderApi = {
  list: (status, keyword) => request.get('/orders', { params: { status, keyword } }),
  getById: (id) => request.get(`/orders/${id}`),
  updateStatus: (id, status) => request.put(`/orders/${id}/status`, { status })
}

// ==================== 后台加料管理 ====================
export const addonApi = {
  list: () => request.get('/addons'),
  create: data => request.post('/addons', data),
  update: (id, data) => request.put(`/addons/${id}`, data),
  delete: id => request.delete(`/addons/${id}`)
}

// ==================== 活动管理 ====================
export const postApi = {
  list: () => request.get('/posts'),
  create: data => request.post('/posts', data),
  delete: id => request.delete(`/posts/${id}`),
  comments: (id) => request.get(`/posts/${id}/comments`),
  addComment: (id, content, parentId) => request.post(`/posts/${id}/comments`, { content, parentId })
}

// ==================== 库存管理 ====================
export const inventoryApi = {
  list: () => request.get('/inventory'),
  create: data => request.post('/inventory', data),
  update: (id, data) => request.put(`/inventory/${id}`, data),
  updateStock: (id, stock) => request.put(`/inventory/${id}/stock`, { stock }),
  delete: id => request.delete(`/inventory/${id}`),
  stats: () => request.get('/inventory/stats')
}

// ==================== 数据统计 ====================
export const dashboardApi = {
  stats: () => request.get('/dashboard/stats'),
  recentOrders: (minutes) => request.get('/dashboard/recent-orders', { params: { minutes } })
}

// ==================== 轮播图管理 ====================
export const bannerApi = {
  list: () => request.get('/banners'),
  create: data => request.post('/banners', data),
  update: (id, data) => request.put(`/banners/${id}`, data),
  delete: id => request.delete(`/banners/${id}`)
}

// ==================== 顾客端 API（需登录） ====================
export const customerApi = {
  login: data => request.post('/customer/auth/login', data),
  register: data => request.post('/customer/auth/register', data),
  getProfile: () => request.get('/customer/me'),
  updateProfile: data => request.put('/customer/profile', data),
  myOrders: () => request.get('/customer/orders')
}

// ==================== 套餐管理 ====================
export const comboApi = {
  list: () => request.get('/combos'),
  get: (id) => request.get(`/combos/${id}`),
  create: data => request.post('/combos', data),
  update: (id, data) => request.put(`/combos/${id}`, data),
  delete: id => request.delete(`/combos/${id}`)
}
