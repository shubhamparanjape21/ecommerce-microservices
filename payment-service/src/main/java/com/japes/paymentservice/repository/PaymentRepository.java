package com.japes.paymentservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.paymentservice.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	public Optional<Payment> findByPaymentReference(String paymentReference);
	public Optional<Payment> findByOrderNumber(String orderNumber);
}
