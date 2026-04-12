<template>
  <div class="app-shell">
    <Sidebar :sidebar-open="sidebarOpen" @navigate="closeSidebarOnMobile" />
    <div v-if="sidebarOpen" class="sidebar-backdrop" @click="closeSidebarOnMobile"></div>

    <div class="content-shell">
      <Topbar @toggle-sidebar="toggleSidebar" />
      <main class="page-shell">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Topbar from './components/Topbar.vue'

const sidebarOpen = ref(true)

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

const closeSidebarOnMobile = () => {
  if (window.innerWidth < 992) {
    sidebarOpen.value = false
  }
}
</script>

<style>
:root {
  font-family: "IBM Plex Sans", "Segoe UI", sans-serif;
  color: #16324f;
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.16), transparent 32%),
    radial-gradient(circle at top right, rgba(20, 184, 166, 0.14), transparent 28%),
    linear-gradient(135deg, #f6fbff 0%, #eef6ff 42%, #f7fbfd 100%);
  --panel-bg: rgba(255, 255, 255, 0.88);
  --panel-border: rgba(148, 163, 184, 0.24);
  --panel-soft: rgba(241, 245, 249, 0.86);
  --text-main: #16324f;
  --text-subtle: #5f7691;
  --accent: #0ea5e9;
  --accent-strong: #0284c7;
  --success: #0f9f6e;
  --warning: #c98916;
  --danger: #d94f62;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  min-width: 320px;
  min-height: 100vh;
  color: var(--text-main);
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.16), transparent 32%),
    radial-gradient(circle at top right, rgba(20, 184, 166, 0.14), transparent 28%),
    linear-gradient(135deg, #f6fbff 0%, #eef6ff 42%, #f7fbfd 100%);
}

a {
  color: inherit;
}

#app {
  min-height: 100vh;
}

.app-shell {
  min-height: 100vh;
  display: flex;
  position: relative;
}

.content-shell {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.page-shell {
  flex: 1;
  padding: 1.5rem;
  overflow: auto;
}

.sidebar-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(71, 85, 105, 0.24);
  z-index: 20;
  display: none;
}

.glass-panel {
  background: var(--panel-bg);
  border: 1px solid var(--panel-border);
  border-radius: 24px;
  box-shadow: 0 22px 48px rgba(148, 163, 184, 0.18);
  backdrop-filter: blur(14px);
}

.section-title {
  font-size: 0.85rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--accent);
  margin-bottom: 0.75rem;
}

.muted-copy {
  color: var(--text-subtle);
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.4rem 0.8rem;
  border-radius: 999px;
  font-size: 0.82rem;
  font-weight: 600;
  border: 1px solid rgba(148, 163, 184, 0.16);
}

.status-pill::before {
  content: "";
  width: 0.55rem;
  height: 0.55rem;
  border-radius: 50%;
  background: currentColor;
}

.status-ok {
  color: var(--success);
  background: rgba(15, 159, 110, 0.1);
}

.status-warn {
  color: var(--warning);
  background: rgba(201, 137, 22, 0.1);
}

.status-down {
  color: var(--danger);
  background: rgba(217, 79, 98, 0.1);
}

.table-surface {
  width: 100%;
  border-collapse: collapse;
}

.table-surface th,
.table-surface td {
  padding: 0.95rem 1rem;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
  vertical-align: middle;
}

.table-surface th {
  color: #59738f;
  font-size: 0.78rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.table-surface tbody tr:hover {
  background: rgba(14, 165, 233, 0.06);
}

.control-input,
.control-select {
  width: 100%;
  padding: 0.85rem 1rem;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.28);
  background: rgba(248, 250, 252, 0.96);
  color: var(--text-main);
}

.control-input:focus,
.control-select:focus {
  outline: none;
  border-color: rgba(14, 165, 233, 0.65);
  box-shadow: 0 0 0 4px rgba(14, 165, 233, 0.12);
}

.btn-accent {
  border: none;
  color: #07111f;
  background: linear-gradient(135deg, #4cc9f0, #67e8f9);
  font-weight: 700;
}

.btn-accent:hover {
  background: linear-gradient(135deg, #67e8f9, #7dd3fc);
  color: #07111f;
}

@media (max-width: 991.98px) {
  .page-shell {
    padding: 1rem;
  }

  .sidebar-backdrop {
    display: block;
  }
}
</style>
