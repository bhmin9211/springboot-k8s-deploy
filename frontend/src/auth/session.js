import { reactive } from 'vue'
import api from '../axios'

const state = reactive({
  ready: false,
  isAuthenticated: false,
  user: null
})

const normalizeRoles = (roles = []) =>
  roles
    .filter(Boolean)
    .map(role => role.startsWith('ROLE_') ? role.slice(5) : role)
    .map(role => role.toUpperCase())
    .sort()

const normalizeUser = (payload) => {
  if (!payload || payload.authenticated === false) {
    return null
  }

  const roles = normalizeRoles(payload.roles || [])
  const access = payload.access || {}

  return {
    username: payload.username || payload.name || 'unknown',
    email: payload.email || '',
    name: payload.name || '',
    roles,
    access: {
      canView: access.canView ?? roles.length > 0,
      canOperate: access.canOperate ?? (roles.includes('OPERATOR') || roles.includes('ADMIN')),
      canAdmin: access.canAdmin ?? roles.includes('ADMIN')
    }
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
