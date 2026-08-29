package com.japes.orderservice.service;

import org.springframework.stereotype.Service;

import com.japes.orderservice.client.PaymentClient;
import com.japes.orderservice.dto.client.CreatePaymentRequest;
import com.japes.orderservice.dto.client.PaymentResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentClientService {
	private final PaymentClient paymentClient;

    @CircuitBreaker(
        name = "paymentService",
        fallbackMethod = "createPaymentFallback"
    )
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        log.info(
            "Calling Payment Service to create payment for order {}",
            request.getOrderNumber()
        );

        return paymentClient.createPayment(request);
    }

    public PaymentResponse createPaymentFallback(
            CreatePaymentRequest request,
            Throwable ex) {

        log.error(
            "Payment Service unavailable for order {}",
            request.getOrderNumber(),
            ex
        );

        throw new RuntimeException(
            "Payment Service is currently unavailable. Please try again later."
        );
    }

}
