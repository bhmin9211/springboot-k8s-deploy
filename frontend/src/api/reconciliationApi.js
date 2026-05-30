import { http } from './http'

export const runReconciliation = (body) => http.post('/reconciliations', body)

export const getReconciliations = (params) => http.get('/reconciliations', { params })

export const getMismatchReconciliations = (params) => http.get('/reconciliations/mismatches', { params })
