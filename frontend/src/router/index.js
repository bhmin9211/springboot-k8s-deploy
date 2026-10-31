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

const requiresAuth = { requiresAuth: true }

const routes = [
  { path: '/', component: Home },
  { path: '/payment-requests', component: PaymentRequestListView, meta: requiresAuth },
  { path: '/payment-requests/:id', component: PaymentRequestDetailView, meta: requiresAuth },
  { path: '/settlements', component: SettlementListView, meta: requiresAuth },
  { path: '/reconciliations', component: ReconciliationListView, meta: requiresAuth },
  { path: '/cluster', component: Cluster, meta: requiresAuth },
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
