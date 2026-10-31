<template>
  <section class="about-grid">
    <div class="glass-panel about-hero">
      <p class="section-title">이 화면</p>
      <h2 class="about-title">결제 요청이 승인되고 정산으로 넘어가는 과정을 다룹니다</h2>
      <p class="muted-copy about-copy">
        접수, 승인, 정산, 대사를 한 화면에서 처리합니다.
        로그인한 사람만 업무 API를 호출하고, 클러스터 변경 명령은 설정으로 꺼 둡니다.
      </p>
    </div>

    <div class="glass-panel timeline-card">
      <p class="section-title">담아 둔 것</p>
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
      <p class="section-title">구성</p>
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
    title: '같은 요청은 한 건',
    text: '같은 멱등키와 같은 내용이면 기존 요청을 돌려주고, 내용이 다르면 새로 만들지 않습니다.'
  },
  {
    step: '02',
    title: '권한마다 할 수 있는 일이 다름',
    text: '조회는 로그인한 사람이 하고, 승인과 정산, 상태 변경은 운영자와 관리자만 합니다.'
  },
  {
    step: '03',
    title: '클러스터 명령은 기본으로 꺼 둠',
    text: '조회는 로그인 뒤에 가능하고, 삭제나 스케일은 별도 설정이 켜져 있을 때만 실행됩니다.'
  }
]

const stacks = [
  { name: '서버', value: 'Spring Boot, Security, JPA, OAuth2 Login' },
  { name: '화면', value: 'Vue 3, Vite, Bootstrap, Axios' },
  { name: '배포', value: 'GitHub Actions, Docker, Helm' },
  { name: '실행 환경', value: 'Kubernetes, MariaDB, Keycloak' }
]
</script>
