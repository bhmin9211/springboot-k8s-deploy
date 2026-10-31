<template>
  <section class="overview-grid">
    <div class="hero-card glass-panel">
      <div class="hero-copy">
        <p class="section-title">결제 운영</p>
        <h2 class="hero-title">승인할 건부터 봅니다</h2>
        <p class="muted-copy hero-text">
          성공한 요청만 정산할 수 있습니다.
        </p>
        <div class="hero-actions">
          <router-link to="/payment-requests" class="btn btn-accent px-4 py-2">요청 보기</router-link>
          <router-link to="/settlements" class="ghost-link">정산 보기</router-link>
        </div>
      </div>

      <div class="hero-status">
        <p class="section-title mb-2">연결</p>
        <div class="status-stack">
          <div :class="['status-pill', apiStatus.ok ? 'status-ok' : 'status-down']">
            API {{ apiStatus.ok ? '정상' : '끊김' }}
          </div>
          <div :class="['status-pill', dbStatus.ok ? 'status-ok' : 'status-warn']">
            DB {{ dbStatus.ok ? '정상' : '확인 필요' }}
          </div>
        </div>
      </div>
    </div>

    <div class="metrics-grid">
      <article v-for="metric in metrics" :key="metric.label" class="metric-card glass-panel">
        <p class="metric-label">{{ metric.label }}</p>
        <strong class="metric-value">{{ metric.value }}</strong>
        <span class="metric-note">{{ metric.note }}</span>
      </article>
    </div>

    <div class="table-card glass-panel">
      <div class="card-head">
        <div>
          <h3 class="mb-0">최근 요청</h3>
        </div>
        <router-link to="/payment-requests" class="ghost-link">전체 목록</router-link>
      </div>
      <div class="table-responsive">
        <table class="table-surface">
          <thead>
            <tr>
              <th>요청번호</th>
              <th>금액</th>
              <th>상태</th>
              <th>요청자</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="request in recentRequests" :key="request.id">
              <td>
                <router-link :to="`/payment-requests/${request.id}`">{{ request.requestNo }}</router-link>
              </td>
              <td>{{ Number(request.amount || 0).toLocaleString() }}</td>
              <td>
                <span :class="['status-pill', statusClass(request.status)]">{{ paymentStatusLabel(request.status) }}</span>
              </td>
              <td>{{ request.requestedBy }}</td>
            </tr>
            <tr v-if="!recentRequests.length">
              <td colspan="4" class="empty-state">
                {{ user ? '등록된 요청이 없습니다.' : '로그인하면 요청 목록이 보입니다.' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../axios'
import { paymentStatusLabel } from '../labels'

const requests = ref([])
const user = ref(null)
const apiStatus = ref({ ok: false })
const dbStatus = ref({ ok: false })

const countStatus = (status) => requests.value.filter(request => request.status === status).length

const metrics = computed(() => [
  { label: '승인 대기', value: user.value ? countStatus('REQUESTED') : '-', note: '아직 승인 전' },
  { label: '처리 중', value: user.value ? countStatus('PROCESSING') + countStatus('RETRYING') : '-', note: '외부 처리' },
  { label: '정산 대기', value: user.value ? countStatus('SUCCESS') : '-', note: '정산 생성 가능' },
  { label: '정산 완료', value: user.value ? countStatus('SETTLED') : '-', note: '정산 데이터 있음' }
])

const recentRequests = computed(() => requests.value.slice(0, 5))

const readCurrentUser = async () => {
  try {
    const response = await api.get('/auth/me')
    user.value = response.data
  } catch (error) {
    user.value = null
  }
}

const loadOverview = async () => {
  const [apiHealthRes, dbHealthRes, paymentRes] = await Promise.allSettled([
    api.get('/auth/health'),
    api.get('/health/healthcheck'),
    user.value ? api.get('/payment-requests') : Promise.resolve(null)
  ])

  apiStatus.value = { ok: apiHealthRes.status === 'fulfilled' }
  dbStatus.value = {
    ok: dbHealthRes.status === 'fulfilled' && dbHealthRes.value.data === 'OK'
  }
  requests.value = paymentRes.status === 'fulfilled' && paymentRes.value
    ? paymentRes.value.data
    : []
}

const statusClass = (status) => {
  if (['SUCCESS', 'SETTLED'].includes(status)) {
    return 'status-ok'
  }
  if (['FAILED', 'REJECTED'].includes(status)) {
    return 'status-down'
  }
  return 'status-warn'
}

onMounted(async () => {
  await readCurrentUser()
  await loadOverview()
})
</script>
