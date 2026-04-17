<template>
  <section class="login-layout">
    <div class="glass-panel login-card">
      <p class="section-title">Identity Access</p>
      <h2 class="login-title">Keycloak 기반 운영 대시보드 로그인</h2>
      <p class="muted-copy mb-4">
        메인 페이지는 서비스 UI를 유지하고, 실제 인증과 세션 관리는 백엔드가 Keycloak과 처리합니다.
      </p>

      <div class="login-form">
        <button type="button" class="btn btn-accent w-100 py-3" :disabled="loading" @click="login">
          {{ loading ? 'Redirecting...' : 'Continue with Keycloak' }}
        </button>
      </div>

      <p v-if="message" :class="['feedback', isError ? 'feedback-error' : 'feedback-ok']">
        {{ message }}
      </p>
    </div>

    <div class="glass-panel access-card">
      <p class="section-title">Session Model</p>
      <div class="access-row">
        <span>Browser</span>
        <strong>HttpOnly Session</strong>
      </div>
      <div class="access-row">
        <span>Backend</span>
        <strong>Keycloak Token Owner</strong>
      </div>
      <p class="muted-copy small mb-0">
        프론트는 토큰을 직접 보관하지 않고, 로그인 상태는 `/auth/me` 응답으로 복원합니다.
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
