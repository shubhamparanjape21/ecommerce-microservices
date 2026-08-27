package com.japes.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.japes.orderservice.config.FeignConfig;
import com.japes.orderservice.dto.client.CreatePaymentRequest;
import com.japes.orderservice.dto.client.PaymentInitiationResponse;
import com.japes.orderservice.dto.client.PaymentResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "payment-service",configuration = FeignConfig.class)
public interface PaymentClient {
	@PostMapping("/api/v1/payments")
    public PaymentResponse createPayment(@RequestBody CreatePaymentRequest request);

    @PostMapping("/api/v1/payments/initiate/{paymentReference}")
    public PaymentInitiationResponse initiatePayment(@PathVariable String paymentReference);
}
