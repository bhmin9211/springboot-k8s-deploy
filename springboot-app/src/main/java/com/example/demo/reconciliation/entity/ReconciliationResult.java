package com.example.demo.reconciliation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReconciliationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settlement_id")
    private Long settlementId;

    @Column(name = "external_transaction_id", nullable = false, length = 100)
    private String externalTransactionId;

    @Column(name = "internal_amount", precision = 15, scale = 2)
    private BigDecimal internalAmount;

    @Column(name = "external_amount", precision = 15, scale = 2)
    private BigDecimal externalAmount;

    @Column(name = "internal_status", length = 30)
    private String internalStatus;

    @Column(name = "external_status", length = 30)
    private String externalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_type", nullable = false, length = 30)
    private ReconciliationResultType resultType;

    @Column(name = "mismatch_reason", length = 500)
    private String mismatchReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private ReconciliationResult(Long settlementId, String externalTransactionId, BigDecimal internalAmount,
                                 BigDecimal externalAmount, String internalStatus, String externalStatus,
                                 ReconciliationResultType resultType, String mismatchReason) {
        this.settlementId = settlementId;
        this.externalTransactionId = externalTransactionId;
        this.internalAmount = internalAmount;
        this.externalAmount = externalAmount;
        this.internalStatus = internalStatus;
        this.externalStatus = externalStatus;
        this.resultType = resultType;
        this.mismatchReason = mismatchReason;
    }

    public static ReconciliationResult of(Long settlementId, String externalTransactionId, BigDecimal internalAmount,
                                          BigDecimal externalAmount, String internalStatus, String externalStatus,
                                          ReconciliationResultType resultType, String mismatchReason) {
        return new ReconciliationResult(
                settlementId,
                externalTransactionId,
                internalAmount,
                externalAmount,
                internalStatus,
                externalStatus,
                resultType,
                mismatchReason
        );
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
