package com.example.demo.reconciliation.dto;

import com.example.demo.reconciliation.entity.ReconciliationResult;
import com.example.demo.reconciliation.entity.ReconciliationResultType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReconciliationResultResponse(
        Long id,
        Long settlementId,
        String externalTransactionId,
        BigDecimal internalAmount,
        BigDecimal externalAmount,
        String internalStatus,
        String externalStatus,
        ReconciliationResultType resultType,
        String mismatchReason,
        LocalDateTime createdAt
) {

    public static ReconciliationResultResponse from(ReconciliationResult result) {
        return new ReconciliationResultResponse(
                result.getId(),
                result.getSettlementId(),
                result.getExternalTransactionId(),
                result.getInternalAmount(),
                result.getExternalAmount(),
                result.getInternalStatus(),
                result.getExternalStatus(),
                result.getResultType(),
                result.getMismatchReason(),
                result.getCreatedAt()
        );
    }
}
