<template>
  <section class="cluster-layout">
    <div class="glass-panel filter-bar">
      <div class="filter-grid">
        <div>
          <label class="filter-label" for="namespace">네임스페이스</label>
          <select id="namespace" v-model="selectedNamespace" class="control-select">
            <option value="all">전체</option>
            <option v-for="namespace in namespaces" :key="namespace" :value="namespace">
              {{ namespace }}
            </option>
          </select>
        </div>
        <div>
          <label class="filter-label" for="resourceType">리소스</label>
          <select id="resourceType" v-model="resourceType" class="control-select">
            <option value="pods">파드</option>
            <option value="deployments">디플로이먼트</option>
            <option value="services">서비스</option>
          </select>
        </div>
        <div>
          <label class="filter-label" for="query">검색</label>
          <input id="query" v-model="searchQuery" class="control-input" placeholder="이름, 네임스페이스" />
        </div>
      </div>
    </div>

    <div class="cluster-grid">
      <div class="glass-panel list-panel">
        <div class="card-head">
          <div>
            <p class="section-title mb-1">{{ resourceTitle }}</p>
            <h3 class="mb-0">{{ filteredItems.length }}건</h3>
          </div>
          <span class="muted-copy">{{ accessLabel }}</span>
        </div>

        <div class="table-responsive">
          <table class="table-surface">
            <thead>
              <tr v-if="resourceType === 'pods'">
                <th>이름</th>
                <th>상태</th>
                <th>네임스페이스</th>
                <th>이미지</th>
              </tr>
              <tr v-else-if="resourceType === 'deployments'">
                <th>이름</th>
                <th>준비</th>
                <th>네임스페이스</th>
                <th>이미지</th>
              </tr>
              <tr v-else>
                <th>이름</th>
                <th>종류</th>
                <th>네임스페이스</th>
                <th>포트</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="item in filteredItems"
                :key="itemKey(item)"
                class="clickable-row"
                @click="selectedItem = item"
              >
                <template v-if="resourceType === 'pods'">
                  <td>{{ item.name }}</td>
                  <td><span :class="['status-pill', statusClass(item.status)]">{{ item.status || 'Unknown' }}</span></td>
                  <td>{{ item.namespace || '-' }}</td>
                  <td>{{ item.image || '-' }}</td>
                </template>

                <template v-else-if="resourceType === 'deployments'">
                  <td>{{ item.name }}</td>
                  <td>{{ item.readyReplicas || 0 }}/{{ item.replicas || 0 }}</td>
                  <td>{{ item.namespace || '-' }}</td>
                  <td>{{ item.image || '-' }}</td>
                </template>

                <template v-else>
                  <td>{{ item.name }}</td>
                  <td>{{ item.type || '-' }}</td>
                  <td>{{ item.namespace || '-' }}</td>
                  <td>{{ item.ports?.join(', ') || '-' }}</td>
                </template>
              </tr>

              <tr v-if="!filteredItems.length">
                <td colspan="4" class="empty-state">
                  연결된 Kubernetes 데이터가 없거나 필터 결과가 없습니다.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <aside class="glass-panel detail-panel">
        <p class="section-title mb-1">선택한 리소스</p>
        <template v-if="selectedItem">
          <h3 class="detail-title">{{ selectedItem.name }}</h3>
          <div class="detail-list">
            <div class="detail-row">
              <span>네임스페이스</span>
              <strong>{{ selectedItem.namespace || '-' }}</strong>
            </div>
            <div class="detail-row">
              <span>상태</span>
              <strong>{{ selectedItem.status || selectedItem.type || '-' }}</strong>
            </div>
            <div class="detail-row">
              <span>이미지</span>
              <strong>{{ selectedItem.image || '-' }}</strong>
            </div>
            <div class="detail-row">
              <span>생성</span>
              <strong>{{ selectedItem.creationTimestamp || '-' }}</strong>
            </div>
            <div class="detail-row" v-if="resourceType === 'pods'">
              <span>재시작</span>
              <strong>{{ selectedItem.restartCount || '0' }}</strong>
            </div>
            <div class="detail-row" v-if="resourceType === 'deployments'">
              <span>복제본</span>
              <strong>{{ selectedItem.readyReplicas || 0 }}/{{ selectedItem.replicas || 0 }}</strong>
            </div>
            <div class="detail-row" v-if="resourceType === 'services'">
              <span>포트</span>
              <strong>{{ selectedItem.ports?.join(', ') || '-' }}</strong>
            </div>
          </div>
        </template>

        <p v-else class="muted-copy mb-0">
          왼쪽 표에서 리소스를 선택하면 상세 정보가 표시됩니다.
        </p>
      </aside>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import api from '../axios'
import { sessionState } from '../auth/session'

const namespaces = ref([])
const pods = ref([])
const deployments = ref([])
const services = ref([])
const resourceType = ref('pods')
const selectedNamespace = ref('all')
const searchQuery = ref('')
const selectedItem = ref(null)

const datasets = computed(() => ({
  pods: pods.value,
  deployments: deployments.value,
  services: services.value
}))

const resourceTitle = computed(() => {
  if (resourceType.value === 'deployments') {
    return '디플로이먼트'
  }
  if (resourceType.value === 'services') {
    return '서비스'
  }
  return '파드'
})

const accessLabel = computed(() => {
  const roles = sessionState.user?.roles || []
  if (roles.includes('ADMIN')) {
    return '관리자'
  }
  if (roles.includes('OPERATOR')) {
    return '운영자'
  }
  if (roles.includes('VIEWER')) {
    return '조회'
  }
  return '로그인 필요'
})

const filteredItems = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  return datasets.value[resourceType.value]
    .filter(item => selectedNamespace.value === 'all' || item.namespace === selectedNamespace.value)
    .filter(item => !query || [item.name, item.namespace, item.image, item.type].filter(Boolean).join(' ').toLowerCase().includes(query))
})

const itemKey = (item) => `${item.namespace || 'all'}-${item.name}`

const statusClass = (value) => {
  if (!value) {
    return 'status-warn'
  }
  const lowered = value.toLowerCase()
  if (lowered.includes('run') || lowered.includes('ready')) {
    return 'status-ok'
  }
  if (lowered.includes('pending')) {
    return 'status-warn'
  }
  return 'status-down'
}

const loadClusterData = async () => {
  const [namespaceRes, podRes, deploymentRes, serviceRes] = await Promise.allSettled([
    api.get('/k8s/namespaces'),
    api.get('/k8s/pods'),
    api.get('/k8s/deployments'),
    api.get('/k8s/services')
  ])

  if (namespaceRes.status === 'fulfilled') {
    namespaces.value = namespaceRes.value.data
  }
  if (podRes.status === 'fulfilled') {
    pods.value = podRes.value.data
  }
  if (deploymentRes.status === 'fulfilled') {
    deployments.value = deploymentRes.value.data
  }
  if (serviceRes.status === 'fulfilled') {
    services.value = serviceRes.value.data
  }

  selectedItem.value = filteredItems.value[0] || null
}

watch([resourceType, selectedNamespace, searchQuery], () => {
  selectedItem.value = filteredItems.value[0] || null
})

onMounted(loadClusterData)
</script>
