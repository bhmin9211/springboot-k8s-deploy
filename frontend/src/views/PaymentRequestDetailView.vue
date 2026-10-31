<template>
  <section class="ops-stack">
    <div class="glass-panel ops-header">
      <div>
        <h2 class="mb-1">{{ request?.requestNo || `요청 ${id}` }}</h2>
        <p class="muted-copy mb-0">상태, 변경 이력, 감사 로그를 이 화면에서 확인합니다.</p>
      </div>
      <router-link to="/payment-requests" class="btn btn-outline-secondary">목록으로</router-link>
    </div>

    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-if="loading" class="glass-panel empty-state">불러오는 중입니다.</div>

    <template v-else-if="request">
      <div class="glass-panel ops-card">
        <div class="detail-grid">
          <div v-for="item in detailItems" :key="item.label" class="detail-item">
            <span>{{ item.label }}</span>
            <strong>{{ item.value || '-' }}</strong>
          </div>
        </div>

        <div class="action-row mt-4">
          <button v-if="request.status === 'REQUESTED'" class="btn btn-primary" type="button" @click="runAction('approve')">승인</button>
          <button v-if="request.status === 'REQUESTED'" class="btn btn-outline-danger" type="button" @click="runAction('reject')">반려</button>
          <button v-if="request.status === 'APPROVED'" class="btn btn-primary" type="button" @click="runAction('process')">처리 시작</button>
          <button v-if="['PROCESSING', 'RETRYING'].includes(request.status)" class="btn btn-success" type="button" @click="runAction('success')">성공 처리</button>
          <button v-if="['PROCESSING', 'RETRYING'].includes(request.status)" class="btn btn-outline-danger" type="button" @click="runAction('fail')">실패 처리</button>
          <button v-if="request.status === 'FAILED'" class="btn btn-warning" type="button" @click="runAction('retry')">재처리</button>
          <span v-if="terminalStatus" class="muted-copy small">현재 상태에서는 실행 가능한 액션이 없습니다.</span>
        </div>
      </div>

      <div class="glass-panel ops-card">
        <div class="card-head">
          <div>
            <h3 class="mb-0">상태 변경 이력</h3>
          </div>
        </div>
        <div class="table-responsive">
          <table class="table-surface">
            <thead>
              <tr>
                <th>이전</th>
                <th>이후</th>
                <th>사유</th>
                <th>변경자</th>
                <th>일시</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="history in histories" :key="history.id">
                <td>{{ paymentStatusLabel(history.fromStatus) }}</td>
                <td>{{ paymentStatusLabel(history.toStatus) }}</td>
                <td>{{ history.reason || '-' }}</td>
                <td>{{ history.changedBy }}</td>
                <td>{{ formatDateTime(history.createdAt) }}</td>
              </tr>
              <tr v-if="!histories.length">
                <td colspan="5" class="empty-state">상태 이력이 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="glass-panel ops-card">
        <div class="card-head">
          <div>
            <h3 class="mb-0">감사 로그</h3>
          </div>
        </div>
        <div class="table-responsive">
          <table class="table-surface">
            <thead>
              <tr>
                <th>동작</th>
                <th>대상</th>
                <th>수행자</th>
                <th>이전</th>
                <th>이후</th>
                <th>IP</th>
                <th>일시</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in auditLogs" :key="log.id">
                <td>{{ log.actionType }}</td>
                <td>{{ log.targetType }} #{{ log.targetId }}</td>
                <td>{{ log.actorId }}</td>
                <td class="log-cell">{{ log.beforeValue || '-' }}</td>
                <td class="log-cell">{{ log.afterValue || '-' }}</td>
                <td>{{ log.ipAddress || '-' }}</td>
                <td>{{ formatDateTime(log.createdAt) }}</td>
              </tr>
              <tr v-if="!auditLogs.length">
                <td colspan="7" class="empty-state">감사로그가 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getAuditLogs } from '../api/auditLogApi'
import { sessionState, startLogin } from '../auth/session'
import {
  approvePaymentRequest,
  failPaymentRequest,
  getPaymentRequest,
  getPaymentRequestHistories,
  processPaymentRequest,
  rejectPaymentRequest,
  retryPaymentRequest,
  successPaymentRequest
} from '../api/paymentRequestApi'
import { formatDateTime } from '../formatDate'
import { paymentStatusLabel } from '../labels'

const route = useRoute()
const id = route.params.id
const request = ref(null)
const histories = ref([])
const auditLogs = ref([])
const loading = ref(false)
const error = ref('')

const detailItems = computed(() => [
  { label: 'ID', value: request.value.id },
  { label: '요청번호', value: request.value.requestNo },
  { label: '멱등키', value: request.value.idempotencyKey },
  { label: '금액', value: Number(request.value.amount || 0).toLocaleString() },
  { label: '상태', value: paymentStatusLabel(request.value.status) },
  { label: '요청자', value: request.value.requestedBy },
  { label: '승인자', value: request.value.approvedBy },
  { label: '승인일시', value: formatDateTime(request.value.approvedAt) },
  { label: '반려자', value: request.value.rejectedBy },
  { label: '반려일시', value: formatDateTime(request.value.rejectedAt) },
  { label: '반려사유', value: request.value.rejectReason },
  { label: '실패사유', value: request.value.failureReason },
  { label: '재처리횟수', value: request.value.retryCount },
  { label: '생성일시', value: formatDateTime(request.value.createdAt) },
  { label: '수정일시', value: formatDateTime(request.value.updatedAt) }
])

const terminalStatus = computed(() => ['SUCCESS', 'SETTLED', 'REJECTED'].includes(request.value?.status))

const loadDetail = async () => {
  loading.value = true
  error.value = ''
  try {
    const [detailRes, historyRes, auditRes] = await Promise.all([
      getPaymentRequest(id),
      getPaymentRequestHistories(id),
      getAuditLogs({ targetType: 'PAYMENT_REQUEST', targetId: id })
    ])
    request.value = detailRes.data
    histories.value = historyRes.data
    auditLogs.value = auditRes.data
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
}

const runAction = async (action) => {
  error.value = ''
  if (!sessionState.isAuthenticated) {
    startLogin(`/payment-requests/${id}`)
    return
  }
  try {
    if (action === 'approve') await approvePaymentRequest(id)
    if (action === 'reject') await rejectPaymentRequest(id, { reason: '운영자 반려 처리' })
    if (action === 'process') await processPaymentRequest(id)
    if (action === 'success') await successPaymentRequest(id)
    if (action === 'fail') await failPaymentRequest(id, { reason: '외부 API 처리 중 일시 오류가 발생했습니다.' })
    if (action === 'retry') await retryPaymentRequest(id)
    await loadDetail()
  } catch (err) {
    error.value = errorMessage(err)
  }
}

const errorMessage = (err) => err.response?.data?.message || err.message || '요청 처리 중 오류가 발생했습니다.'

onMounted(loadDetail)
</script>
