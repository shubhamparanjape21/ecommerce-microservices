package com.japes.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.paymentservice.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
