<template>
  <section class="about-grid">
    <div class="glass-panel about-hero">
      <p class="section-title">Project Story</p>
      <h2 class="about-title">GitOps 파이프라인을 눈으로 보여주는 포트폴리오</h2>
      <p class="muted-copy about-copy">
        이 프로젝트는 단순한 CRUD가 아니라, 인증, 백엔드 API, 프론트 대시보드, Kubernetes 조회,
        Helm 배포, Argo CD 동기화를 한 서비스 흐름으로 묶는 데 초점을 맞췄습니다.
      </p>
    </div>

    <div class="glass-panel timeline-card">
      <p class="section-title">Build Intent</p>
      <div class="timeline-list">
        <div v-for="item in highlights" :key="item.title" class="timeline-item">
          <span class="timeline-marker">{{ item.step }}</span>
          <div>
            <h3>{{ item.title }}</h3>
            <p class="muted-copy mb-0">{{ item.text }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="glass-panel stack-card">
      <p class="section-title">Stack</p>
      <div class="stack-grid">
        <div v-for="stack in stacks" :key="stack.name" class="stack-item">
          <strong>{{ stack.name }}</strong>
          <span>{{ stack.value }}</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const highlights = [
  {
    step: '01',
    title: 'Public Portfolio Shape',
    text: '이력서에서 바로 열어볼 수 있는 공개 URL과 운영형 화면 구성을 목표로 정리했습니다.'
  },
  {
    step: '02',
    title: 'Read-only Kubernetes Access',
    text: '공개 환경에서는 조회 위주로 구성하고, 명령형 API는 비활성화 가능한 구조로 안전하게 분리했습니다.'
  },
  {
    step: '03',
    title: 'Deploy Anywhere Configuration',
    text: 'DB, JWT, CORS, API 주소를 환경변수화해 Render와 Vercel 배포 기준으로 맞췄습니다.'
  }
]

const stacks = [
  { name: 'Backend', value: 'Spring Boot, Security, JPA, JWT' },
  { name: 'Frontend', value: 'Vue 3, Vite, Bootstrap, Axios' },
  { name: 'Delivery', value: 'GitHub Actions, Docker, Helm, Argo CD' },
  { name: 'Platform', value: 'Kubernetes, MariaDB, Render, Vercel' }
]
</script>

<style scoped>
.about-grid {
  display: grid;
  gap: 1.25rem;
}

.about-hero,
.timeline-card,
.stack-card {
  padding: 1.5rem;
}

.about-title {
  font-size: clamp(1.8rem, 3vw, 2.7rem);
  margin-bottom: 1rem;
}

.about-copy {
  max-width: 68ch;
}

.timeline-list {
  display: grid;
  gap: 1rem;
}

.timeline-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  border-radius: 18px;
  background: rgba(148, 163, 184, 0.05);
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.timeline-marker {
  width: 46px;
  height: 46px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: rgba(76, 201, 240, 0.12);
  color: var(--accent);
  font-size: 0.85rem;
  font-weight: 700;
  flex-shrink: 0;
}

.timeline-item h3 {
  font-size: 1.05rem;
  margin-bottom: 0.45rem;
}

.stack-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.stack-item {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
  padding: 1rem;
  border-radius: 18px;
  background: rgba(76, 201, 240, 0.06);
  border: 1px solid rgba(76, 201, 240, 0.1);
}

.stack-item span {
  color: var(--text-subtle);
}

@media (max-width: 767.98px) {
  .stack-grid {
    grid-template-columns: 1fr;
  }

  .timeline-item {
    flex-direction: column;
  }
}
</style>
