package com.example.demo.payment.dto;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        String idempotencyKey,
        BigDecimal amount,
        String requestedBy
) {
}
