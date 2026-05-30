package com.example.demo.reconciliation.service;

import com.example.demo.audit.service.AuditLogService;
import com.example.demo.reconciliation.dto.ReconcileSettlementRequest;
import com.example.demo.reconciliation.dto.ReconciliationResultResponse;
import com.example.demo.reconciliation.entity.ReconciliationResult;
import com.example.demo.reconciliation.entity.ReconciliationResultType;
import com.example.demo.reconciliation.repository.ReconciliationResultRepository;
import com.example.demo.settlement.entity.Settlement;
import com.example.demo.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReconciliationService {

    private static final String TARGET_TYPE = "RECONCILIATION_RESULT";

    private final ReconciliationResultRepository reconciliationResultRepository;
    private final SettlementService settlementService;
    private final AuditLogService auditLogService;

    @Transactional
    public ReconciliationResultResponse reconcile(ReconcileSettlementRequest request, String actorId, String ipAddress) {
        validate(request);

        ReconciliationResult result;
        if (request.settlementId() == null) {
            result = ReconciliationResult.of(
                    null,
                    request.externalTransactionId(),
                    null,
                    request.externalAmount(),
                    null,
                    request.externalStatus(),
                    ReconciliationResultType.INTERNAL_MISSING,
                    "내부 정산 데이터가 존재하지 않습니다."
            );
        } else {
            Settlement settlement = settlementService.getSettlementEntity(request.settlementId());
            result = compare(settlement, request);
        }

        ReconciliationResult saved = reconciliationResultRepository.save(result);
        auditLogService.record(
                actorId,
                "RECONCILE",
                TARGET_TYPE,
                saved.getId(),
                null,
                "{\"resultType\":\"" + saved.getResultType() + "\"}",
                ipAddress
        );

        return ReconciliationResultResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ReconciliationResultResponse> findAll() {
        return reconciliationResultRepository.findAll().stream()
                .map(ReconciliationResultResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReconciliationResultResponse> findMismatches() {
        return reconciliationResultRepository.findByResultTypeInOrderByCreatedAtDesc(
                        EnumSet.of(
                                ReconciliationResultType.AMOUNT_MISMATCH,
                                ReconciliationResultType.STATUS_MISMATCH,
                                ReconciliationResultType.EXTERNAL_MISSING,
                                ReconciliationResultType.INTERNAL_MISSING
                        )
                ).stream()
                .map(ReconciliationResultResponse::from)
                .toList();
    }

    private ReconciliationResult compare(Settlement settlement, ReconcileSettlementRequest request) {
        BigDecimal internalAmount = settlement.getSettlementAmount();
        String internalStatus = settlement.getSettlementStatus().name();
        String externalStatus = request.externalStatus();

        if (request.externalAmount() == null) {
            return ReconciliationResult.of(
                    settlement.getId(),
                    request.externalTransactionId(),
                    internalAmount,
                    null,
                    internalStatus,
                    externalStatus,
                    ReconciliationResultType.EXTERNAL_MISSING,
                    "외부 정산 금액이 전달되지 않았습니다."
            );
        }

        if (internalAmount.compareTo(request.externalAmount()) != 0) {
            return ReconciliationResult.of(
                    settlement.getId(),
                    request.externalTransactionId(),
                    internalAmount,
                    request.externalAmount(),
                    internalStatus,
                    externalStatus,
                    ReconciliationResultType.AMOUNT_MISMATCH,
                    "내부 정산 금액과 외부 정산 금액이 일치하지 않습니다."
            );
        }

        if (externalStatus != null && !externalStatus.isBlank() && !internalStatus.equalsIgnoreCase(externalStatus)) {
            return ReconciliationResult.of(
                    settlement.getId(),
                    request.externalTransactionId(),
                    internalAmount,
                    request.externalAmount(),
                    internalStatus,
                    externalStatus,
                    ReconciliationResultType.STATUS_MISMATCH,
                    "내부 정산 상태와 외부 정산 상태가 일치하지 않습니다."
            );
        }

        return ReconciliationResult.of(
                settlement.getId(),
                request.externalTransactionId(),
                internalAmount,
                request.externalAmount(),
                internalStatus,
                externalStatus,
                ReconciliationResultType.MATCHED,
                null
        );
    }

    private void validate(ReconcileSettlementRequest request) {
        if (request.externalTransactionId() == null || request.externalTransactionId().isBlank()) {
            throw new IllegalArgumentException("externalTransactionId는 필수입니다.");
        }
    }
}
