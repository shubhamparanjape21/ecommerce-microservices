package com.japes.orderservice.dto.client;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInitiationResponse {

    private String paymentReference;

    private String razorpayOrderId;

    private BigDecimal amount;

    private String currency;

    private String paymentStatus;

    private String razorpayKeyId;
}