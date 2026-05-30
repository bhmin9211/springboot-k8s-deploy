import { createRouter, createWebHistory } from 'vue-router'
import { restoreSession, sessionState } from '../auth/session'

import Home from '../views/Home.vue'
import Cluster from '../views/Cluster.vue'
import About from '../views/About.vue'
import Login from '../views/Login.vue'
import PaymentRequestListView from '../views/PaymentRequestListView.vue'
import PaymentRequestDetailView from '../views/PaymentRequestDetailView.vue'
import SettlementListView from '../views/SettlementListView.vue'
import ReconciliationListView from '../views/ReconciliationListView.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/payment-requests', component: PaymentRequestListView },
  { path: '/payment-requests/:id', component: PaymentRequestDetailView },
  { path: '/settlements', component: SettlementListView },
  { path: '/reconciliations', component: ReconciliationListView },
  { path: '/cluster', component: Cluster },
  { path: '/about', component: About },
  { path: '/login', component: Login }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  if (!sessionState.ready) {
    await restoreSession()
  }

  if (to.meta.requiresAuth && !sessionState.isAuthenticated) {
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    }
  }

  if (to.path === '/login' && sessionState.isAuthenticated) {
    return '/'
  }
})

export default router
