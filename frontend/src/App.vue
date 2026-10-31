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
