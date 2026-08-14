package com.japes.orderservice.dto.client;

import java.math.BigDecimal;

import com.japes.orderservice.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    private String orderNumber;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
}

