package com.example.demo.reconciliation.dto;

import java.math.BigDecimal;

public record ReconcileSettlementRequest(
        Long settlementId,
        String externalTransactionId,
        BigDecimal externalAmount,
        String externalStatus
) {
}
