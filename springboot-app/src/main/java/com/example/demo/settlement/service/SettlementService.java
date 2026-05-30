package com.example.demo.settlement.service;

import com.example.demo.audit.service.AuditLogService;
import com.example.demo.payment.entity.PaymentRequest;
import com.example.demo.payment.entity.PaymentStatus;
import com.example.demo.payment.service.PaymentRequestService;
import com.example.demo.settlement.dto.GenerateSettlementRequest;
import com.example.demo.settlement.dto.SettlementResponse;
import com.example.demo.settlement.entity.Settlement;
import com.example.demo.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private static final String TARGET_TYPE = "SETTLEMENT";
    private static final BigDecimal FEE_RATE = new BigDecimal("0.025");

    private final SettlementRepository settlementRepository;
    private final PaymentRequestService paymentRequestService;
    private final AuditLogService auditLogService;

    @Transactional
    public SettlementResponse generate(GenerateSettlementRequest request, String actorId, String ipAddress) {
        if (request.paymentRequestId() == null) {
            throw new IllegalArgumentException("paymentRequestId는 필수입니다.");
        }
        if (settlementRepository.existsByPaymentRequestId(request.paymentRequestId())) {
            throw new IllegalStateException("이미 정산 데이터가 생성된 결제 요청입니다. paymentRequestId=" + request.paymentRequestId());
        }

        PaymentRequest paymentRequest = paymentRequestService.getPaymentRequestEntity(request.paymentRequestId());
        if (paymentRequest.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("SUCCESS 상태의 결제 요청만 정산 생성이 가능합니다. status=" + paymentRequest.getStatus());
        }

        BigDecimal grossAmount = paymentRequest.getAmount();
        BigDecimal feeAmount = grossAmount.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal settlementAmount = grossAmount.subtract(feeAmount);
        LocalDate dueDate = request.settlementDueDate() != null ? request.settlementDueDate() : LocalDate.now().plusDays(1);

        Settlement settlement = settlementRepository.save(Settlement.create(
                paymentRequest.getId(),
                grossAmount,
                feeAmount,
                settlementAmount,
                dueDate
        ));
        paymentRequestService.settle(paymentRequest, actorId, ipAddress);

        auditLogService.record(
                actorId,
                "CREATE",
                TARGET_TYPE,
                settlement.getId(),
                null,
                "{\"status\":\"" + settlement.getSettlementStatus() + "\",\"settlementAmount\":" + settlement.getSettlementAmount() + "}",
                ipAddress
        );

        return SettlementResponse.from(settlement);
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> findAll() {
        return settlementRepository.findAll().stream()
                .map(SettlementResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SettlementResponse findById(Long id) {
        return SettlementResponse.from(getSettlement(id));
    }

    @Transactional(readOnly = true)
    public Settlement getSettlementEntity(Long id) {
        return getSettlement(id);
    }

    private Settlement getSettlement(Long id) {
        return settlementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("정산 데이터를 찾을 수 없습니다. id=" + id));
    }
}
