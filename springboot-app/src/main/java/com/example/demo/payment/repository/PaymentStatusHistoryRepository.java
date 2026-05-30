package com.example.demo.payment.repository;

import com.example.demo.payment.entity.PaymentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory, Long> {

    List<PaymentStatusHistory> findByPaymentRequestIdOrderByCreatedAtAsc(Long paymentRequestId);
}
