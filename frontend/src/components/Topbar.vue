<template>
  <header class="topbar glass-panel">
    <div class="d-flex align-items-center gap-3">
      <button class="menu-button d-lg-none" @click="toggleSidebar">
        <span></span>
        <span></span>
        <span></span>
      </button>

      <div>
        <p class="section-title mb-1">결제 백오피스</p>
        <h1 class="topbar-title">{{ pageTitle }}</h1>
      </div>
    </div>

    <div class="topbar-status">
      <div :class="['status-chip', { 'is-on': sessionState.isAuthenticated }]">
        <span class="status-dot"></span>
        {{ sessionState.isAuthenticated ? '로그인됨' : '로그인 필요' }}
      </div>
      <span v-if="sessionState.isAuthenticated" class="identity-chip">
        {{ sessionState.user?.username }}
        <small v-if="primaryRole">{{ primaryRole }}</small>
      </span>
      <button v-if="sessionState.isAuthenticated" type="button" class="signin-link action-button" @click="signOut">
        로그아웃
      </button>
      <router-link v-else to="/login" class="signin-link">로그인</router-link>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { logout, sessionState } from '../auth/session'

const emit = defineEmits(['toggle-sidebar'])
const route = useRoute()

const titleMap = {
  '/': '오늘 볼 요청',
  '/payment-requests': '결제 요청',
  '/settlements': '정산',
  '/reconciliations': '대사',
  '/cluster': '클러스터',
  '/login': '로그인',
  '/about': '소개'
}

const pageTitle = computed(() => {
  if (route.path.startsWith('/payment-requests/')) {
    return '요청 상세'
  }
  return titleMap[route.path] || '결제 운영'
})
const roleNames = {
  ADMIN: '관리자',
  OPERATOR: '운영자',
  VIEWER: '조회'
}

const primaryRole = computed(() => roleNames[sessionState.user?.roles?.[0]] || '')

const toggleSidebar = () => {
  emit('toggle-sidebar')
}

const signOut = async () => {
  await logout()
  window.location.href = '/login'
}
</script>
