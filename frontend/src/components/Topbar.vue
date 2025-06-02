<template>
  <nav class="navbar navbar-expand navbar-light bg-light px-3">
    <!-- Sidebar Toggle Button -->
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
</template>

<script setup>
import { defineEmits } from 'vue'

const emit = defineEmits(['toggle-sidebar'])

const toggleSidebar = () => {
  emit('toggle-sidebar')
}
</script>
