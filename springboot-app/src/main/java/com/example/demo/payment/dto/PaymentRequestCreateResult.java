package com.example.demo.payment.dto;

public record PaymentRequestCreateResult(
        PaymentRequestResponse paymentRequest,
        boolean created
) {
}
