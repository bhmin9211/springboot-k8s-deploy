import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { restoreSession } from './auth/session'

import 'bootstrap/dist/css/bootstrap.min.css'

const bootstrap = async () => {
  await restoreSession()

  const app = createApp(App)
  app.use(router)
  app.mount('#app')
}

bootstrap()
