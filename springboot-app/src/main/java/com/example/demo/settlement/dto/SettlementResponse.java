package com.example.demo.settlement.dto;

import com.example.demo.settlement.entity.Settlement;
import com.example.demo.settlement.entity.SettlementStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SettlementResponse(
        Long id,
        Long paymentRequestId,
        BigDecimal grossAmount,
        BigDecimal feeAmount,
        BigDecimal settlementAmount,
        SettlementStatus settlementStatus,
        LocalDate settlementDueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static SettlementResponse from(Settlement settlement) {
        return new SettlementResponse(
                settlement.getId(),
                settlement.getPaymentRequestId(),
                settlement.getGrossAmount(),
                settlement.getFeeAmount(),
                settlement.getSettlementAmount(),
                settlement.getSettlementStatus(),
                settlement.getSettlementDueDate(),
                settlement.getCreatedAt(),
                settlement.getUpdatedAt()
        );
    }
}
