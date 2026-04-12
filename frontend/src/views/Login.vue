<template>
  <section class="login-layout">
    <div class="glass-panel login-card">
      <p class="section-title">Identity Access</p>
      <h2 class="login-title">JWT 기반 운영 대시보드 로그인</h2>
      <p class="muted-copy mb-4">
        Render에 배포된 백엔드와 연결되면 인증 토큰이 저장되고 이후 클러스터 조회 요청에 자동으로 포함됩니다.
      </p>

      <form class="login-form" @submit.prevent="login">
        <div>
          <label class="form-label" for="username">아이디</label>
          <input id="username" v-model="username" type="text" class="control-input" required />
        </div>
        <div>
          <label class="form-label" for="password">비밀번호</label>
          <input id="password" v-model="password" type="password" class="control-input" required />
        </div>
        <button type="submit" class="btn btn-accent w-100 py-3" :disabled="loading">
          {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>

      <p v-if="message" :class="['feedback', isError ? 'feedback-error' : 'feedback-ok']">
        {{ message }}
      </p>
    </div>

    <div class="glass-panel access-card">
      <p class="section-title">Demo Account</p>
      <div class="access-row">
        <span>Username</span>
        <strong>demo</strong>
      </div>
      <div class="access-row">
        <span>Password</span>
        <strong>test1234</strong>
      </div>
      <p class="muted-copy small mb-0">
        외부 DB 초기화 스크립트를 적용하면 공개 데모에서 바로 확인할 수 있습니다.
      </p>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../axios'

const username = ref('demo')
const password = ref('test1234')
const loading = ref(false)
const message = ref('')
const isError = ref(false)

const router = useRouter()

const login = async () => {
  loading.value = true
  message.value = ''
  isError.value = false

  try {
    const response = await api.post('/auth/login', {
      username: username.value,
      password: password.value
    })

    localStorage.setItem('jwtToken', response.data.token)
    message.value = '로그인 성공. Overview로 이동합니다.'
    router.push('/')
  } catch (error) {
    isError.value = true
    message.value = error.response?.data?.message || '로그인에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(260px, 0.8fr);
  gap: 1.25rem;
}

.login-card,
.access-card {
  padding: 1.5rem;
}

.login-title {
  margin-bottom: 0.8rem;
  font-size: clamp(1.7rem, 3vw, 2.4rem);
}

.login-form {
  display: grid;
  gap: 1rem;
}

.form-label {
  display: block;
  margin-bottom: 0.45rem;
  color: var(--text-subtle);
}

.feedback {
  margin-top: 1rem;
  padding: 0.9rem 1rem;
  border-radius: 14px;
  font-weight: 600;
}

.feedback-ok {
  background: rgba(52, 211, 153, 0.14);
  color: #86efac;
}

.feedback-error {
  background: rgba(248, 113, 113, 0.14);
  color: #fca5a5;
}

.access-row {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.95rem 0;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

.access-row span {
  color: var(--text-subtle);
}

@media (max-width: 991.98px) {
  .login-layout {
    grid-template-columns: 1fr;
  }
}
</style>
