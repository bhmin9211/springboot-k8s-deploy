<template>
  <div>
    <p>Spring Boot Message: {{ message }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const message = ref('Loading...')

onMounted(async () => {
  const res = await axios.get(import.meta.env.VITE_API_BASE_URL + '/message')
  message.value = res.data
})
</script>
