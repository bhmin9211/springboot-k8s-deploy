<template>
  <aside :class="['sidebar-shell', { 'sidebar-open': sidebarOpen }]">
    <div class="sidebar-brand">
      <div class="brand-mark">K8</div>
      <div>
        <p class="brand-title">KubeOps Dashboard</p>
        <p class="brand-subtitle">GitOps Portfolio</p>
      </div>
    </div>

    <div class="sidebar-group">
      <p class="sidebar-label">Navigation</p>
      <router-link
        v-for="item in navigation"
        :key="item.to"
        :to="item.to"
        class="sidebar-link"
        active-class="sidebar-link-active"
        @click="$emit('navigate')"
      >
        <span class="link-icon">{{ item.icon }}</span>
        <span>
          <strong>{{ item.label }}</strong>
          <small>{{ item.caption }}</small>
        </span>
      </router-link>
    </div>

    <div class="sidebar-card glass-panel">
      <p class="section-title mb-2">Public Demo</p>
      <h3 class="card-title">Read-only mode first</h3>
      <p class="muted-copy small mb-0">
        Kubernetes command APIs stay disabled in public deployments while monitoring views remain open.
      </p>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  sidebarOpen: {
    type: Boolean,
    default: true
  }
})

defineEmits(['navigate'])

const navigation = [
  { to: '/', label: 'Overview', caption: 'Cluster summary', icon: '01' },
  { to: '/cluster', label: 'Cluster', caption: 'Pods and services', icon: '02' },
  { to: '/login', label: 'Login', caption: 'JWT access', icon: '03' },
  { to: '/about', label: 'About', caption: 'Project story', icon: '04' }
]
</script>

<style scoped>
.sidebar-shell {
  width: 300px;
  flex-shrink: 0;
  padding: 1.5rem 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background: linear-gradient(180deg, rgba(240, 249, 255, 0.95), rgba(232, 244, 255, 0.88));
  border-right: 1px solid rgba(148, 163, 184, 0.2);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 0.9rem;
}

.brand-mark {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-weight: 800;
  color: #08111f;
  background: linear-gradient(135deg, #4cc9f0, #34d399);
}

.brand-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
}

.brand-subtitle,
.sidebar-label {
  margin: 0;
  color: var(--text-subtle);
  font-size: 0.82rem;
}

.sidebar-group {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.sidebar-link {
  display: flex;
  align-items: center;
  gap: 0.9rem;
  padding: 0.9rem 1rem;
  border-radius: 18px;
  text-decoration: none;
  color: var(--text-main);
  transition: background 0.2s ease, transform 0.2s ease;
}

.sidebar-link:hover {
  background: rgba(14, 165, 233, 0.08);
  transform: translateX(2px);
}

.sidebar-link-active {
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.12), rgba(45, 212, 191, 0.12));
  border: 1px solid rgba(14, 165, 233, 0.2);
}

.link-icon {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  color: var(--accent);
  background: rgba(14, 165, 233, 0.1);
  font-size: 0.8rem;
  font-weight: 700;
}

.sidebar-link strong,
.sidebar-link small {
  display: block;
}

.sidebar-link small {
  color: var(--text-subtle);
  margin-top: 0.15rem;
}

.sidebar-card {
  padding: 1.25rem;
  margin-top: auto;
  background: rgba(255, 255, 255, 0.78);
}

.card-title {
  font-size: 1.05rem;
  margin-bottom: 0.5rem;
}

@media (max-width: 991.98px) {
  .sidebar-shell {
    position: fixed;
    inset: 0 auto 0 0;
    z-index: 30;
    transform: translateX(-100%);
    transition: transform 0.25s ease;
  }

  .sidebar-open {
    transform: translateX(0);
  }
}
</style>
