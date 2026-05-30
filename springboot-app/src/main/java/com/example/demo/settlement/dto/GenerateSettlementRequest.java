package com.example.demo.settlement.dto;

import java.time.LocalDate;

public record GenerateSettlementRequest(
        Long paymentRequestId,
        LocalDate settlementDueDate
) {
}
