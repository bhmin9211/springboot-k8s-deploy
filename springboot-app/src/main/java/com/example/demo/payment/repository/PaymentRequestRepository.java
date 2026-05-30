package com.example.demo.payment.repository;

import com.example.demo.payment.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

    Optional<PaymentRequest> findByIdempotencyKey(String idempotencyKey);
}
