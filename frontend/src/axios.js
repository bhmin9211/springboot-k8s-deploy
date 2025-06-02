import axios from 'axios'

const instance = axios.create({
    baseURL: 'http://localhost:8081/api',
    timeout: 5000,
})

// 요청 인터셉터 → JWT 자동 추가
instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('jwtToken')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

export default instance
