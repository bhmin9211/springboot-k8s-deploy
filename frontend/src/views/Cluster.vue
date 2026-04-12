<template>
  <section class="cluster-layout">
    <div class="glass-panel filter-bar">
      <div class="filter-grid">
        <div>
          <label class="filter-label" for="namespace">Namespace</label>
          <select id="namespace" v-model="selectedNamespace" class="control-select">
            <option value="all">All namespaces</option>
            <option v-for="namespace in namespaces" :key="namespace" :value="namespace">
              {{ namespace }}
            </option>
          </select>
        </div>
        <div>
          <label class="filter-label" for="resourceType">Resource</label>
          <select id="resourceType" v-model="resourceType" class="control-select">
            <option value="pods">Pods</option>
            <option value="deployments">Deployments</option>
            <option value="services">Services</option>
          </select>
        </div>
        <div>
          <label class="filter-label" for="query">Search</label>
          <input id="query" v-model="searchQuery" class="control-input" placeholder="api, default, ingress..." />
        </div>
      </div>
    </div>

    <div class="cluster-grid">
      <div class="glass-panel list-panel">
        <div class="card-head">
          <div>
            <p class="section-title mb-1">{{ resourceTitle }}</p>
            <h3 class="mb-0">{{ filteredItems.length }} resources visible</h3>
          </div>
          <span class="muted-copy">Read-only explorer</span>
        </div>

        <div class="table-responsive">
          <table class="table-surface">
            <thead>
              <tr v-if="resourceType === 'pods'">
                <th>Name</th>
                <th>Status</th>
                <th>Namespace</th>
                <th>Image</th>
              </tr>
              <tr v-else-if="resourceType === 'deployments'">
                <th>Name</th>
                <th>Ready</th>
                <th>Namespace</th>
                <th>Image</th>
              </tr>
              <tr v-else>
                <th>Name</th>
                <th>Type</th>
                <th>Namespace</th>
                <th>Ports</th>
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
        <p class="section-title mb-1">Resource Detail</p>
        <template v-if="selectedItem">
          <h3 class="detail-title">{{ selectedItem.name }}</h3>
          <div class="detail-list">
            <div class="detail-row">
              <span>Namespace</span>
              <strong>{{ selectedItem.namespace || '-' }}</strong>
            </div>
            <div class="detail-row">
              <span>Status</span>
              <strong>{{ selectedItem.status || selectedItem.type || '-' }}</strong>
            </div>
            <div class="detail-row">
              <span>Image</span>
              <strong>{{ selectedItem.image || '-' }}</strong>
            </div>
            <div class="detail-row">
              <span>Created</span>
              <strong>{{ selectedItem.creationTimestamp || '-' }}</strong>
            </div>
            <div class="detail-row" v-if="resourceType === 'pods'">
              <span>Restarts</span>
              <strong>{{ selectedItem.restartCount || '0' }}</strong>
            </div>
            <div class="detail-row" v-if="resourceType === 'deployments'">
              <span>Replicas</span>
              <strong>{{ selectedItem.readyReplicas || 0 }}/{{ selectedItem.replicas || 0 }}</strong>
            </div>
            <div class="detail-row" v-if="resourceType === 'services'">
              <span>Ports</span>
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
    return 'Deployments'
  }
  if (resourceType.value === 'services') {
    return 'Services'
  }
  return 'Pods'
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

<style scoped>
.cluster-layout {
  display: grid;
  gap: 1.25rem;
}

.filter-bar,
.list-panel,
.detail-panel {
  padding: 1.25rem;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
}

.filter-label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-subtle);
  font-size: 0.84rem;
}

.cluster-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(290px, 0.8fr);
  gap: 1.25rem;
}

.clickable-row {
  cursor: pointer;
}

.clickable-row:hover {
  background: rgba(14, 165, 233, 0.08);
}

.detail-title {
  margin-bottom: 1rem;
}

.detail-list {
  display: grid;
  gap: 0.8rem;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
}

.detail-row span {
  color: var(--text-subtle);
}

@media (max-width: 991.98px) {
  .filter-grid,
  .cluster-grid {
    grid-template-columns: 1fr;
  }
}
</style>
