import { reactive } from 'vue'
import api from '../axios'

const state = reactive({
  ready: false,
  isAuthenticated: false,
  user: null
})

const normalizeUser = (payload) => {
  if (!payload || payload.authenticated === false) {
    return null
  }

  return {
    username: payload.username || payload.name || 'unknown',
    email: payload.email || '',
    name: payload.name || '',
    roles: payload.roles || []
  }
}

export const sessionState = state

export const restoreSession = async () => {
  try {
    const response = await api.get('/auth/me')
    state.user = normalizeUser(response.data)
    state.isAuthenticated = Boolean(state.user)
  } catch (error) {
    state.user = null
    state.isAuthenticated = false
  } finally {
    state.ready = true
  }
}

export const startLogin = (redirectPath = window.location.pathname) => {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api'
  const loginPath = import.meta.env.VITE_AUTH_LOGIN_PATH || '/auth/login/keycloak'
  const loginUrl = `${apiBaseUrl}${loginPath}?redirect=${encodeURIComponent(redirectPath)}`
  window.location.href = loginUrl
}

export const logout = async () => {
  const logoutPath = import.meta.env.VITE_AUTH_LOGOUT_PATH || '/auth/logout'
  await api.post(logoutPath)
  state.user = null
  state.isAuthenticated = false
}
