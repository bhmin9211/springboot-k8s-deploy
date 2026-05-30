<template>
  <section class="ops-stack">
    <div class="glass-panel ops-header">
      <div>
        <p class="section-title mb-1">Settlements</p>
        <h2 class="mb-1">정산 운영</h2>
        <p class="muted-copy mb-0">성공 처리된 결제 요청을 기준으로 정산 데이터를 생성합니다.</p>
      </div>
      <button class="btn btn-accent px-4" type="button" @click="loadSettlements">조회</button>
    </div>

    <div class="glass-panel ops-card">
      <div class="ops-form-grid compact">
        <label>
          <span class="field-label">결제요청 ID</span>
          <input v-model="form.paymentRequestId" class="control-input" placeholder="1" type="number" min="1" />
        </label>
        <label>
          <span class="field-label">정산 예정일</span>
          <input v-model="form.settlementDueDate" class="control-input" type="date" />
        </label>
      </div>
      <div class="action-row mt-3">
        <button class="btn btn-outline-primary" type="button" @click="generate">정산 생성</button>
        <span class="muted-copy small">SUCCESS 상태의 결제 요청만 정산 생성이 가능합니다.</span>
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
              <th>Payment ID</th>
              <th>Gross</th>
              <th>Fee</th>
              <th>Settlement</th>
              <th>Status</th>
              <th>Due Date</th>
              <th>Created</th>
              <th>Updated</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="settlement in settlements" :key="settlement.id">
              <td>{{ settlement.id }}</td>
              <td>{{ settlement.paymentRequestId }}</td>
              <td>{{ formatAmount(settlement.grossAmount) }}</td>
              <td>{{ formatAmount(settlement.feeAmount) }}</td>
              <td>{{ formatAmount(settlement.settlementAmount) }}</td>
              <td><span class="status-pill status-ok">{{ settlement.settlementStatus }}</span></td>
              <td>{{ settlement.settlementDueDate }}</td>
              <td>{{ settlement.createdAt }}</td>
              <td>{{ settlement.updatedAt }}</td>
            </tr>
            <tr v-if="!settlements.length">
              <td colspan="9" class="empty-state">정산 데이터가 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { sessionState, startLogin } from '../auth/session'
import { generateSettlement, getSettlements } from '../api/settlementApi'

const settlements = ref([])
const loading = ref(false)
const error = ref('')
const form = ref({
  paymentRequestId: '',
  settlementDueDate: ''
})

const loadSettlements = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await getSettlements()
    settlements.value = response.data
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
}

const generate = async () => {
  error.value = ''
  if (!sessionState.isAuthenticated) {
    startLogin('/settlements')
    return
  }
  try {
    await generateSettlement({
      paymentRequestId: Number(form.value.paymentRequestId),
      settlementDueDate: form.value.settlementDueDate || null
    })
    form.value.paymentRequestId = ''
    await loadSettlements()
  } catch (err) {
    error.value = errorMessage(err)
  }
}

const formatAmount = (value) => Number(value || 0).toLocaleString()

const errorMessage = (err) => err.response?.data?.message || err.message || '요청 처리 중 오류가 발생했습니다.'

onMounted(loadSettlements)
</script>
