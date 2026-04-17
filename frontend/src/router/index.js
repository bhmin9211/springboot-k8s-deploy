import { createRouter, createWebHistory } from 'vue-router'
import { restoreSession, sessionState } from '../auth/session'

import Home from '../views/Home.vue'
import Cluster from '../views/Cluster.vue'
import About from '../views/About.vue'
import Login from '../views/Login.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/cluster', component: Cluster, meta: { requiresAuth: true } },
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
