import { http } from './http'

export const getAuditLogs = (params) => http.get('/audit-logs', { params })
