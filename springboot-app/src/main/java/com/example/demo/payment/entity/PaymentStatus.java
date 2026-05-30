package com.example.demo.payment.entity;

public enum PaymentStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    PROCESSING,
    SUCCESS,
    FAILED,
    RETRYING,
    SETTLED
}
