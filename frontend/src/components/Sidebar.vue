<template>
  <aside :class="['sidebar-shell', { 'sidebar-open': sidebarOpen }]">
    <div class="sidebar-brand">
      <div>
        <p class="brand-title">결제 운영</p>
        <p class="brand-subtitle">요청, 승인, 정산</p>
      </div>
    </div>

    <div class="sidebar-group">
      <p class="sidebar-label">메뉴</p>
      <router-link
        v-for="item in navigation"
        :key="item.to"
        :to="item.to"
        class="sidebar-link"
        active-class="sidebar-link-active"
        @click="$emit('navigate')"
      >
        <span>{{ item.label }}</span>
      </router-link>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  sidebarOpen: {
    type: Boolean,
    default: true
  }
})

defineEmits(['navigate'])

const navigation = [
  { to: '/', label: '홈' },
  { to: '/payment-requests', label: '결제 요청' },
  { to: '/settlements', label: '정산' },
  { to: '/reconciliations', label: '대사' },
  { to: '/cluster', label: '클러스터' }
]
</script>
