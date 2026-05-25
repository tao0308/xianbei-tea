import request from '../utils/request'

export const customerApi = {
  login: data => request.post('/customer/auth/login', data),
  register: data => request.post('/customer/auth/register', data),
  me: () => request.get('/customer/me'),
  categories: () => request.get('/customer/categories'),
  products: (categoryId) => request.get('/customer/products', { params: { categoryId } }),
  addons: () => request.get('/customer/addons'),
  createOrder: data => request.post('/customer/orders', data),
  myOrders: () => request.get('/customer/orders'),
  posts: () => request.get('/customer/posts'),
  toggleLike: (id) => request.post(`/customer/posts/${id}/like`),
  checkLike: (id) => request.get(`/customer/posts/${id}/like`),
  comments: (id) => request.get(`/customer/posts/${id}/comments`),
  addComment: (id, content, parentId) => request.post(`/customer/posts/${id}/comments`, { content, parentId }),
  updateProfile: (data) => request.put('/customer/profile', data),
  banners: () => request.get('/public/banners'),
  combos: () => request.get('/public/combos')
}

export const addressApi = {
  list: () => request.get('/customer/address'),
  create: data => request.post('/customer/address', data),
  update: (id, data) => request.put(`/customer/address/${id}`, data),
  setDefault: (id) => request.put(`/customer/address/${id}/default`),
  delete: id => request.delete(`/customer/address/${id}`)
}
