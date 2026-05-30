import { http } from './http'

export const generateSettlement = (body) => http.post('/settlements/generate', body)

export const getSettlements = (params) => http.get('/settlements', { params })

export const getSettlement = (id) => http.get(`/settlements/${id}`)
