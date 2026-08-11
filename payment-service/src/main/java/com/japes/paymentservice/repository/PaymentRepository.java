package com.japes.paymentservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.paymentservice.entity.Payment;
import com.japes.paymentservice.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	public Optional<Payment> findByPaymentReference(String paymentReference);
	public Optional<Payment> findByOrderNumber(String orderNumber);
	Page<Payment> findByPaymentStatus(PaymentStatus status, Pageable pageable);
}
