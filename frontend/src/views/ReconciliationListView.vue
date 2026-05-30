<template>
  <section class="ops-stack">
    <div class="glass-panel ops-header">
      <div>
        <p class="section-title mb-1">Reconciliations</p>
        <h2 class="mb-1">정산 대사</h2>
        <p class="muted-copy mb-0">내부 정산 데이터와 외부 정산 결과의 금액/상태 차이를 확인합니다.</p>
      </div>
      <div class="action-row">
        <button class="btn btn-outline-primary" type="button" @click="loadAll">전체 조회</button>
        <button class="btn btn-accent px-4" type="button" @click="loadMismatches">불일치만 보기</button>
      </div>
    </div>

    <div class="glass-panel ops-card">
      <div class="ops-form-grid">
        <label>
          <span class="field-label">정산 ID</span>
          <input v-model="form.settlementId" class="control-input" placeholder="1" type="number" min="1" />
        </label>
        <label>
          <span class="field-label">외부 거래 ID</span>
          <input v-model="form.externalTransactionId" class="control-input" placeholder="EXT-TRX-001" />
        </label>
        <label>
          <span class="field-label">외부 금액</span>
          <input v-model="form.externalAmount" class="control-input" placeholder="117000" type="number" min="0" />
        </label>
        <label>
          <span class="field-label">외부 상태</span>
          <input v-model="form.externalStatus" class="control-input" placeholder="CREATED" />
        </label>
      </div>
      <div class="action-row mt-3">
        <button class="btn btn-outline-primary" type="button" @click="run">대사 실행</button>
        <span class="muted-copy small">정산 ID를 비우면 내부 누락 케이스로 기록됩니다.</span>
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
              <th>Settlement</th>
              <th>External TRX</th>
              <th>Internal Amount</th>
              <th>External Amount</th>
              <th>Internal Status</th>
              <th>External Status</th>
              <th>Result</th>
              <th>Reason</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="result in results" :key="result.id">
              <td>{{ result.id }}</td>
              <td>{{ result.settlementId || '-' }}</td>
              <td>{{ result.externalTransactionId }}</td>
              <td>{{ formatAmount(result.internalAmount) }}</td>
              <td>{{ formatAmount(result.externalAmount) }}</td>
              <td>{{ result.internalStatus || '-' }}</td>
              <td>{{ result.externalStatus || '-' }}</td>
              <td><span :class="['status-pill', resultClass(result.resultType)]">{{ result.resultType }}</span></td>
              <td>{{ result.mismatchReason || '-' }}</td>
              <td>{{ result.createdAt }}</td>
            </tr>
            <tr v-if="!results.length">
              <td colspan="10" class="empty-state">대사 결과가 없습니다.</td>
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
import { getMismatchReconciliations, getReconciliations, runReconciliation } from '../api/reconciliationApi'

const results = ref([])
const loading = ref(false)
const error = ref('')
const form = ref({
  settlementId: '',
  externalTransactionId: `EXT-${Date.now()}`,
  externalAmount: '',
  externalStatus: 'CREATED'
})

const loadAll = async () => {
  await load(() => getReconciliations())
}

const loadMismatches = async () => {
  await load(() => getMismatchReconciliations())
}

const load = async (loader) => {
  loading.value = true
  error.value = ''
  try {
    const response = await loader()
    results.value = response.data
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
}

const run = async () => {
  error.value = ''
  if (!sessionState.isAuthenticated) {
    startLogin('/reconciliations')
    return
  }
  try {
    await runReconciliation({
      settlementId: form.value.settlementId ? Number(form.value.settlementId) : null,
      externalTransactionId: form.value.externalTransactionId,
      externalAmount: form.value.externalAmount === '' ? null : Number(form.value.externalAmount),
      externalStatus: form.value.externalStatus
    })
    form.value.externalTransactionId = `EXT-${Date.now()}`
    await loadAll()
  } catch (err) {
    error.value = errorMessage(err)
  }
}

const resultClass = (type) => type === 'MATCHED' ? 'status-ok' : 'status-down'

const formatAmount = (value) => value === null || value === undefined ? '-' : Number(value).toLocaleString()

const errorMessage = (err) => err.response?.data?.message || err.message || '요청 처리 중 오류가 발생했습니다.'

onMounted(loadAll)
</script>
