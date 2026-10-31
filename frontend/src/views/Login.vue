<template>
  <section class="login-layout">
    <div class="glass-panel login-card">
      <p class="section-title">로그인</p>
      <h2 class="login-title">운영자 계정으로 들어갑니다</h2>
      <p class="muted-copy mb-4">
        비밀번호 확인은 Keycloak이 하고, 이 화면은 세션만 사용합니다.
      </p>

      <div class="login-form">
        <button type="button" class="btn btn-accent w-100 py-3" :disabled="loading" @click="login">
          {{ loading ? '이동 중' : 'Keycloak으로 로그인' }}
        </button>
      </div>

      <p v-if="message" :class="['feedback', isError ? 'feedback-error' : 'feedback-ok']">
        {{ message }}
      </p>
    </div>

  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { startLogin } from '../auth/session'

const loading = ref(false)
const message = ref('')
const isError = ref(false)

const route = useRoute()

const login = async () => {
  loading.value = true
  message.value = ''
  isError.value = false

  try {
    startLogin(route.query.redirect || '/')
  } catch (error) {
    isError.value = true
    message.value = '로그인을 시작하지 못했습니다.'
    loading.value = false
  }
}
</script>
