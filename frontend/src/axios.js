import axios from 'axios'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api',
  timeout: 5000,
  withCredentials: true
})

const UNSAFE_METHODS = new Set(['post', 'put', 'patch', 'delete'])

let csrfPromise = null

const loadCsrf = () => {
  if (!csrfPromise) {
    csrfPromise = instance.get('/auth/csrf')
      .then(response => response.data)
      .catch(error => {
        csrfPromise = null
        throw error
      })
  }
  return csrfPromise
}

instance.interceptors.request.use(async config => {
  const method = (config.method || 'get').toLowerCase()
  if (!UNSAFE_METHODS.has(method) || config.url?.includes('/auth/csrf')) {
    return config
  }

  const csrf = await loadCsrf()
  config.headers[csrf.headerName] = csrf.token
  return config
})

instance.interceptors.response.use(response => response, async error => {
  const config = error.config
  const message = error.response?.data?.message
  if (error.response?.status !== 403 || !message?.includes('CSRF') || !config || config._csrfRetried) {
    return Promise.reject(error)
  }

  config._csrfRetried = true
  csrfPromise = null
  const csrf = await loadCsrf()
  config.headers[csrf.headerName] = csrf.token
  return instance.request(config)
})

export default instance
