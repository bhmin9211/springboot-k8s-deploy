<template>
  <header class="topbar glass-panel">
    <div class="d-flex align-items-center gap-3">
      <button class="menu-button d-lg-none" @click="toggleSidebar">
        <span></span>
        <span></span>
        <span></span>
      </button>

      <div>
        <p class="section-title mb-1">Live Overview</p>
        <h1 class="topbar-title">{{ pageTitle }}</h1>
      </div>
    </div>

    <div class="topbar-status">
      <div class="status-chip">
        <span class="status-dot"></span>
        {{ sessionState.isAuthenticated ? 'Session Active' : 'Public Demo Ready' }}
      </div>
      <span v-if="sessionState.isAuthenticated" class="identity-chip">
        {{ sessionState.user?.username }}
        <small v-if="primaryRole">{{ primaryRole }}</small>
      </span>
      <button v-if="sessionState.isAuthenticated" type="button" class="signin-link action-button" @click="signOut">
        Logout
      </button>
      <router-link v-else to="/login" class="signin-link">Access</router-link>
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
  '/': 'Overview Dashboard',
  '/cluster': 'Cluster Explorer',
  '/login': 'Identity Access',
  '/about': 'Project Brief'
}

const pageTitle = computed(() => titleMap[route.path] || 'KubeOps Dashboard')
const primaryRole = computed(() => sessionState.user?.roles?.[0] || '')

const toggleSidebar = () => {
  emit('toggle-sidebar')
}

const signOut = async () => {
  await logout()
  window.location.href = '/login'
}
</script>

<style scoped>
.topbar {
  margin: 1.5rem 1.5rem 0;
  padding: 1rem 1.25rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.topbar-title {
  margin: 0;
  font-size: clamp(1.2rem, 2vw, 1.8rem);
}

.topbar-status {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.status-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.75rem 1rem;
  border-radius: 999px;
  background: rgba(15, 159, 110, 0.12);
  color: #0f9f6e;
  font-size: 0.88rem;
  font-weight: 600;
}

.status-dot {
  width: 0.55rem;
  height: 0.55rem;
  border-radius: 50%;
  background: currentColor;
}

.signin-link {
  padding: 0.78rem 1.15rem;
  border-radius: 999px;
  text-decoration: none;
  background: rgba(14, 165, 233, 0.12);
  color: var(--accent);
  font-weight: 700;
}

.identity-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.78rem 1rem;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.06);
  color: var(--text-main);
  font-weight: 700;
}

.identity-chip small {
  color: var(--text-subtle);
  font-size: 0.72rem;
  text-transform: uppercase;
}

.action-button {
  border: 0;
}

.menu-button {
  width: 46px;
  height: 46px;
  padding: 0;
  border: 0;
  border-radius: 14px;
  background: rgba(148, 163, 184, 0.14);
  display: grid;
  place-items: center;
}

.menu-button span {
  display: block;
  width: 18px;
  height: 2px;
  border-radius: 999px;
  background: #33506f;
  margin: 2px 0;
}

@media (max-width: 991.98px) {
  .topbar {
    margin: 1rem 1rem 0;
    align-items: flex-start;
    flex-direction: column;
  }

  .topbar-status {
    width: 100%;
    justify-content: space-between;
    flex-wrap: wrap;
  }
}
</style>
