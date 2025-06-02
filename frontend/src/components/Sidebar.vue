<template>
  <nav :class="['bg-dark text-white p-3', sidebarOpen ? 'd-block' : 'd-none d-md-block']" style="width: 250px;">
    <h2 class="mb-4">테스트</h2>
    <router-link to="/" class="d-block mb-2 text-white text-decoration-none">메인</router-link>
    <router-link to="/About" class="d-block mb-2 text-white text-decoration-none">pod</router-link>
    <router-link to="/Contact" class="d-block mb-2 text-white text-decoration-none">deploy</router-link>
  </nav>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  sidebarOpen: Boolean
})
</script>
