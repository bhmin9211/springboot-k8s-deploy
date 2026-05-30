package com.example.demo.settlement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "settlements",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_settlements_payment_request_id", columnNames = "payment_request_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_request_id", nullable = false)
    private Long paymentRequestId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal grossAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal feeAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal settlementAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SettlementStatus settlementStatus;

    @Column(nullable = false)
    private LocalDate settlementDueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Settlement(Long paymentRequestId, BigDecimal grossAmount, BigDecimal feeAmount,
                       BigDecimal settlementAmount, LocalDate settlementDueDate) {
        this.paymentRequestId = paymentRequestId;
        this.grossAmount = grossAmount;
        this.feeAmount = feeAmount;
        this.settlementAmount = settlementAmount;
        this.settlementDueDate = settlementDueDate;
        this.settlementStatus = SettlementStatus.CREATED;
    }

    public static Settlement create(Long paymentRequestId, BigDecimal grossAmount, BigDecimal feeAmount,
                                    BigDecimal settlementAmount, LocalDate settlementDueDate) {
        return new Settlement(paymentRequestId, grossAmount, feeAmount, settlementAmount, settlementDueDate);
    }

    public void confirm() {
        if (settlementStatus != SettlementStatus.CREATED) {
            throw new IllegalStateException("확정 가능한 정산 상태가 아닙니다. status=" + settlementStatus);
        }
        this.settlementStatus = SettlementStatus.CONFIRMED;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
