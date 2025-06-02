<template>
  <div class="d-flex vh-100">
    <!-- Sidebar 컴포넌트 -->
    <Sidebar :sidebarOpen="sidebarOpen" />

    <!-- Main Content -->
    <div class="flex-grow-1 d-flex flex-column">
      <!-- Topbar 컴포넌트 -->
      <Topbar @toggle-sidebar="toggleSidebar" />

      <!-- Content -->
      <main class="p-4 flex-grow-1 overflow-auto">
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
</script>