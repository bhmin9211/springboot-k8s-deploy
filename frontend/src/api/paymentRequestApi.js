import { http } from './http'

export const createPaymentRequest = (body) => http.post('/payment-requests', body)

export const getPaymentRequests = (params) => http.get('/payment-requests', { params })

export const getPaymentRequest = (id) => http.get(`/payment-requests/${id}`)

export const approvePaymentRequest = (id) => http.post(`/payment-requests/${id}/approve`)

export const rejectPaymentRequest = (id, body) => http.post(`/payment-requests/${id}/reject`, body)

export const processPaymentRequest = (id) => http.post(`/payment-requests/${id}/process`)

export const successPaymentRequest = (id) => http.post(`/payment-requests/${id}/success`)

export const failPaymentRequest = (id, body) => http.post(`/payment-requests/${id}/fail`, body)

export const retryPaymentRequest = (id) => http.post(`/payment-requests/${id}/retry`)

export const getPaymentRequestHistories = (id) => http.get(`/payment-requests/${id}/histories`)
