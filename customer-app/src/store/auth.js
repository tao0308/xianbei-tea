import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('customer_token') || '')
  const username = ref(localStorage.getItem('customer_username') || '')
  const realName = ref(localStorage.getItem('customer_realName') || '')

  const isLoggedIn = computed(() => !!token.value)

  function setUser(data) {
    token.value = data.token
    username.value = data.username || ''
    realName.value = data.realName || data.username || ''
    localStorage.setItem('customer_token', data.token)
    localStorage.setItem('customer_username', data.username || '')
    localStorage.setItem('customer_realName', data.realName || data.username || '')
  }

  function logout() {
    token.value = ''
    username.value = ''
    realName.value = ''
    localStorage.removeItem('customer_token')
    localStorage.removeItem('customer_username')
    localStorage.removeItem('customer_realName')
  }

  return { token, username, realName, isLoggedIn, setUser, logout }
})
