package com.example.demo.settlement.repository;

import com.example.demo.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    boolean existsByPaymentRequestId(Long paymentRequestId);

    Optional<Settlement> findByPaymentRequestId(Long paymentRequestId);
}
