<template>
  <section class="ops-stack">
    <div class="glass-panel ops-header">
      <div>
        <p class="section-title mb-1">Payment Requests</p>
        <h2 class="mb-1">결제 요청 운영</h2>
        <p class="muted-copy mb-0">멱등키로 접수된 요청을 조회하고 상태별로 확인합니다.</p>
      </div>
      <button class="btn btn-accent px-4" type="button" @click="loadRequests">조회</button>
    </div>

    <div class="glass-panel ops-card">
      <div class="ops-form-grid">
        <label>
          <span class="field-label">상태</span>
          <select v-model="selectedStatus" class="control-select">
            <option value="">전체</option>
            <option v-for="status in statuses" :key="status" :value="status">{{ status }}</option>
          </select>
        </label>
        <label>
          <span class="field-label">요청 생성</span>
          <input v-model="newRequest.amount" class="control-input" placeholder="금액" type="number" min="1" />
        </label>
        <label>
          <span class="field-label">요청자</span>
          <input v-model="newRequest.requestedBy" class="control-input" placeholder="external-order-api" />
        </label>
        <label>
          <span class="field-label">멱등키</span>
          <input v-model="newRequest.idempotencyKey" class="control-input" placeholder="PAY-ORDER-0001" />
        </label>
      </div>
      <div class="action-row mt-3">
        <button class="btn btn-outline-primary" type="button" @click="createRequest">요청 등록</button>
        <span class="muted-copy small">동일 멱등키로 재요청하면 기존 요청이 반환됩니다.</span>
      </div>
    </div>

    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-if="loading" class="glass-panel empty-state">불러오는 중입니다.</div>

    <div v-else class="glass-panel ops-card">
      <div class="table-responsive">
        <table class="table-surface">
          <thead>
            <tr>
              <th>ID</th>
              <th>Request No</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Requested By</th>
              <th>Created</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="request in filteredRequests" :key="request.id">
              <td>{{ request.id }}</td>
              <td>{{ request.requestNo }}</td>
              <td>{{ formatAmount(request.amount) }}</td>
              <td><span :class="['status-pill', statusClass(request.status)]">{{ request.status }}</span></td>
              <td>{{ request.requestedBy }}</td>
              <td>{{ request.createdAt }}</td>
              <td>
                <router-link :to="`/payment-requests/${request.id}`" class="btn btn-sm btn-outline-primary">
                  상세
                </router-link>
              </td>
            </tr>
            <tr v-if="!filteredRequests.length">
              <td colspan="7" class="empty-state">조회된 결제 요청이 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { sessionState, startLogin } from '../auth/session'
import { createPaymentRequest, getPaymentRequests } from '../api/paymentRequestApi'

const statuses = ['REQUESTED', 'APPROVED', 'REJECTED', 'PROCESSING', 'SUCCESS', 'FAILED', 'RETRYING', 'SETTLED']
const requests = ref([])
const selectedStatus = ref('')
const loading = ref(false)
const error = ref('')
const newRequest = ref({
  idempotencyKey: `PAY-ORDER-${Date.now()}`,
  amount: 120000,
  requestedBy: 'external-order-api'
})

const filteredRequests = computed(() => {
  if (!selectedStatus.value) {
    return requests.value
  }
  return requests.value.filter(request => request.status === selectedStatus.value)
})

const loadRequests = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await getPaymentRequests()
    requests.value = response.data
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
}

const createRequest = async () => {
  error.value = ''
  if (!sessionState.isAuthenticated) {
    startLogin('/payment-requests')
    return
  }
  try {
    await createPaymentRequest({
      idempotencyKey: newRequest.value.idempotencyKey,
      amount: Number(newRequest.value.amount),
      requestedBy: newRequest.value.requestedBy
    })
    newRequest.value.idempotencyKey = `PAY-ORDER-${Date.now()}`
    await loadRequests()
  } catch (err) {
    error.value = errorMessage(err)
  }
}

const formatAmount = (value) => Number(value || 0).toLocaleString()

const statusClass = (status) => {
  if (['SUCCESS', 'SETTLED'].includes(status)) {
    return 'status-ok'
  }
  if (['FAILED', 'REJECTED'].includes(status)) {
    return 'status-down'
  }
  return 'status-warn'
}

const errorMessage = (err) => err.response?.data?.message || err.message || '요청 처리 중 오류가 발생했습니다.'

onMounted(loadRequests)
</script>
