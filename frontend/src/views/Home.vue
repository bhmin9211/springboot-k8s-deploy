<template>
  <section class="overview-grid">
    <div class="hero-card glass-panel">
      <div class="hero-copy">
        <p class="section-title">Kubernetes + GitOps</p>
        <h2 class="hero-title">운영 상태와 배포 흐름을 한 화면에 담는 DevOps 포트폴리오</h2>
        <p class="muted-copy hero-text">
          Spring Boot, Vue, Helm, Argo CD를 연결해 만든 공개형 대시보드입니다. 실제 API가 연결되면
          클러스터 상태와 인증 흐름이 함께 반영됩니다.
        </p>
        <div class="hero-actions">
          <router-link to="/cluster" class="btn btn-accent px-4 py-3">Explore Cluster</router-link>
          <router-link to="/about" class="ghost-link">Read Project Story</router-link>
        </div>
      </div>

      <div class="hero-status glass-panel">
        <p class="section-title mb-2">Connection Status</p>
        <div class="status-stack">
          <div :class="['status-pill', apiStatus.ok ? 'status-ok' : 'status-down']">
            API {{ apiStatus.label }}
          </div>
          <div :class="['status-pill', dbStatus.ok ? 'status-ok' : 'status-warn']">
            DB {{ dbStatus.label }}
          </div>
          <div :class="['status-pill', k8sStatus.ok ? 'status-ok' : 'status-warn']">
            K8s {{ k8sStatus.label }}
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
          <p class="section-title mb-1">Recent Pods</p>
          <h3 class="mb-0">실시간 워크로드 상태</h3>
        </div>
        <router-link to="/cluster" class="ghost-link">Open Cluster View</router-link>
      </div>
      <div class="table-responsive">
        <table class="table-surface">
          <thead>
            <tr>
              <th>Name</th>
              <th>Status</th>
              <th>Namespace</th>
              <th>Restarts</th>
              <th>Node</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="pod in topPods" :key="pod.name">
              <td>{{ pod.name }}</td>
              <td>
                <span :class="['status-pill', podStatusClass(pod.status)]">{{ pod.status || 'Unknown' }}</span>
              </td>
              <td>{{ pod.namespace || '-' }}</td>
              <td>{{ pod.restartCount || '0' }}</td>
              <td>{{ pod.nodeName || '-' }}</td>
            </tr>
            <tr v-if="!topPods.length">
              <td colspan="5" class="empty-state">
                백엔드가 연결되면 Pod 상태가 여기에 표시됩니다.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="pipeline-card glass-panel">
      <div class="card-head">
        <div>
          <p class="section-title mb-1">Delivery Flow</p>
          <h3 class="mb-0">GitOps Delivery Pipeline</h3>
        </div>
      </div>

      <div class="pipeline-steps">
        <div v-for="step in pipelineSteps" :key="step.title" class="pipeline-step">
          <span class="step-index">{{ step.index }}</span>
          <div>
            <strong>{{ step.title }}</strong>
            <p class="muted-copy mb-0">{{ step.caption }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../axios'

const clusterInfo = ref({})
const pods = ref([])
const deployments = ref([])
const services = ref([])
const namespaces = ref([])
const user = ref(null)
const apiStatus = ref({ ok: false, label: 'Offline' })
const dbStatus = ref({ ok: false, label: 'Unknown' })
const k8sStatus = ref({ ok: false, label: 'Unknown' })

const metrics = computed(() => [
  {
    label: 'Pods',
    value: clusterInfo.value.podCount ?? pods.value.length ?? 0,
    note: 'running workloads'
  },
  {
    label: 'Deployments',
    value: clusterInfo.value.deploymentCount ?? deployments.value.length ?? 0,
    note: 'release units'
  },
  {
    label: 'Services',
    value: clusterInfo.value.serviceCount ?? services.value.length ?? 0,
    note: 'traffic endpoints'
  },
  {
    label: 'Namespaces',
    value: clusterInfo.value.namespaceCount ?? namespaces.value.length ?? 0,
    note: 'isolated scopes'
  },
  {
    label: 'Nodes',
    value: clusterInfo.value.nodeCount ?? 0,
    note: 'cluster capacity'
  },
  {
    label: 'User',
    value: user.value?.username || 'Guest',
    note: user.value ? 'authenticated session' : 'public visitor'
  }
])

const topPods = computed(() => pods.value.slice(0, 5))

const pipelineSteps = [
  { index: '01', title: 'Git Push', caption: '코드 변경이 저장소로 올라갑니다.' },
  { index: '02', title: 'GitHub Actions', caption: '이미지 빌드와 태그 업데이트가 실행됩니다.' },
  { index: '03', title: 'Helm Update', caption: '릴리즈 값이 갱신됩니다.' },
  { index: '04', title: 'Argo CD Sync', caption: 'Git 상태와 클러스터 상태를 맞춥니다.' }
]

const readCurrentUser = async () => {
  const token = localStorage.getItem('jwtToken')
  if (!token) {
    return
  }

  try {
    const response = await api.get('/auth/me')
    user.value = response.data
  } catch (error) {
    user.value = null
  }
}

const loadOverview = async () => {
  const [clusterRes, podRes, deployRes, serviceRes, namespaceRes, apiHealthRes, dbHealthRes, k8sHealthRes] =
    await Promise.allSettled([
      api.get('/k8s/cluster/info'),
      api.get('/k8s/pods'),
      api.get('/k8s/deployments'),
      api.get('/k8s/services'),
      api.get('/k8s/namespaces'),
      api.get('/auth/health'),
      api.get('/health/healthcheck'),
      api.get('/k8s/health')
    ])

  if (clusterRes.status === 'fulfilled') {
    clusterInfo.value = clusterRes.value.data
  }
  if (podRes.status === 'fulfilled') {
    pods.value = podRes.value.data
  }
  if (deployRes.status === 'fulfilled') {
    deployments.value = deployRes.value.data
  }
  if (serviceRes.status === 'fulfilled') {
    services.value = serviceRes.value.data
  }
  if (namespaceRes.status === 'fulfilled') {
    namespaces.value = namespaceRes.value.data
  }

  apiStatus.value = apiHealthRes.status === 'fulfilled'
    ? { ok: true, label: 'Healthy' }
    : { ok: false, label: 'Offline' }

  dbStatus.value = dbHealthRes.status === 'fulfilled' && dbHealthRes.value.data === 'OK'
    ? { ok: true, label: 'Healthy' }
    : { ok: false, label: 'Check required' }

  k8sStatus.value = k8sHealthRes.status === 'fulfilled'
    ? { ok: true, label: 'Connected' }
    : { ok: false, label: 'Waiting' }
}

const podStatusClass = (status) => {
  if (!status) {
    return 'status-warn'
  }
  const value = status.toLowerCase()
  if (value.includes('running') || value.includes('ready')) {
    return 'status-ok'
  }
  if (value.includes('pending')) {
    return 'status-warn'
  }
  return 'status-down'
}

onMounted(async () => {
  await Promise.all([loadOverview(), readCurrentUser()])
})
</script>

<style scoped>
.overview-grid {
  display: grid;
  gap: 1.25rem;
}

.hero-card {
  padding: 1.5rem;
  display: grid;
  gap: 1rem;
  grid-template-columns: minmax(0, 1.7fr) minmax(280px, 0.9fr);
}

.hero-title {
  font-size: clamp(2rem, 4vw, 3.2rem);
  line-height: 1.05;
  margin-bottom: 1rem;
}

.hero-text {
  max-width: 60ch;
  font-size: 1rem;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
  flex-wrap: wrap;
}

.ghost-link {
  color: #b8d8ee;
  font-weight: 600;
  text-decoration: none;
}

.hero-status {
  padding: 1.25rem;
  background: rgba(7, 14, 28, 0.72);
}

.status-stack {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 1rem;
}

.metric-card,
.table-card,
.pipeline-card {
  padding: 1.25rem;
}

.metric-label {
  margin-bottom: 0.5rem;
  color: var(--text-subtle);
  font-size: 0.82rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.metric-value {
  display: block;
  font-size: 1.8rem;
  margin-bottom: 0.4rem;
}

.metric-note {
  color: #86a3bf;
  font-size: 0.9rem;
}

.card-head {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
  margin-bottom: 1rem;
}

.empty-state {
  color: var(--text-subtle);
  text-align: center;
  padding: 1.6rem 1rem;
}

.pipeline-steps {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 1rem;
}

.pipeline-step {
  padding: 1rem;
  border-radius: 18px;
  background: rgba(148, 163, 184, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.step-index {
  display: inline-flex;
  margin-bottom: 0.9rem;
  padding: 0.3rem 0.65rem;
  border-radius: 999px;
  background: rgba(76, 201, 240, 0.12);
  color: var(--accent);
  font-size: 0.76rem;
  font-weight: 700;
}

@media (max-width: 1199.98px) {
  .metrics-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 991.98px) {
  .hero-card {
    grid-template-columns: 1fr;
  }

  .pipeline-steps {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 767.98px) {
  .metrics-grid,
  .pipeline-steps {
    grid-template-columns: 1fr;
  }

  .card-head {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
