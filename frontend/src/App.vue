<template>
  <div class="d-flex vh-100">
    <!-- Sidebar -->
    <nav :class="['bg-dark text-white p-3', sidebarOpen ? 'd-block' : 'd-none d-md-block']" style="width: 250px;">
      <h2 class="mb-4">테스트</h2>
      <router-link to="/" class="d-block mb-2 text-white text-decoration-none">메인</router-link>
      <router-link to="/About" class="d-block mb-2 text-white text-decoration-none">pod</router-link>
      <router-link to="/Contact" class="d-block mb-2 text-white text-decoration-none">deploy</router-link>
    </nav>

    <!-- Main Content -->
    <div class="flex-grow-1 d-flex flex-column">
      <!-- Top Navbar -->
      <nav class="navbar navbar-expand navbar-light bg-light px-3">
        <!-- Sidebar Toggle Button (visible on small screens) -->
        <button class="btn btn-outline-primary d-md-none me-2" @click="toggleSidebar">
          ☰
        </button>

        <ul class="nav nav-tabs">
          <li class="nav-item">
            <router-link to="/" class="nav-link" active-class="active" exact>메인</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/About" class="nav-link" active-class="active">pod</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/Contact" class="nav-link" active-class="active">deploy</router-link>
          </li>
        </ul>
      </nav>

      <!-- Content -->
      <main class="p-4 flex-grow-1 overflow-auto">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const sidebarOpen = ref(true)

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}
</script>
