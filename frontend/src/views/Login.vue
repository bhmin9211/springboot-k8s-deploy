<template>
  <div class="container mt-5" style="max-width: 400px;">
    <h2 class="mb-4">로그인</h2>
    <form @submit.prevent="login">
      <div class="mb-3">
        <label for="username" class="form-label">아이디</label>
        <input v-model="username" type="text" class="form-control" id="username" required>
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">비밀번호</label>
        <input v-model="password" type="password" class="form-control" id="password" required>
      </div>
      <button type="submit" class="btn btn-primary w-100">로그인</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')

const router = useRouter()

const login = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/auth/login', {
      username: username.value,
      password: password.value
    })

    const token = response.data.token
    // JWT 토큰 저장 (localStorage 예시)
    localStorage.setItem('jwtToken', token)

    // 로그인 성공 후 홈으로 이동
    router.push('/')
  } catch (error) {
    console.error('로그인 실패:', error)
    alert('로그인 실패!')
  }
}
</script>
