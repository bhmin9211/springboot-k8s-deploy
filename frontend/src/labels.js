const paymentStatusLabels = {
  REQUESTED: '승인 대기',
  APPROVED: '승인',
  REJECTED: '반려',
  PROCESSING: '처리 중',
  SUCCESS: '성공',
  FAILED: '실패',
  RETRYING: '재처리',
  SETTLED: '정산 완료'
}

const settlementStatusLabels = {
  CREATED: '생성',
  CONFIRMED: '확정'
}

const reconciliationResultLabels = {
  MATCHED: '일치',
  AMOUNT_MISMATCH: '금액 불일치',
  STATUS_MISMATCH: '상태 불일치',
  EXTERNAL_MISSING: '외부 없음',
  INTERNAL_MISSING: '내부 없음'
}

export const paymentStatuses = Object.keys(paymentStatusLabels)

export function paymentStatusLabel(status) {
  return paymentStatusLabels[status] || status || '-'
}

export function settlementStatusLabel(status) {
  return settlementStatusLabels[status] || status || '-'
}

export function reconciliationResultLabel(type) {
  return reconciliationResultLabels[type] || type || '-'
}
