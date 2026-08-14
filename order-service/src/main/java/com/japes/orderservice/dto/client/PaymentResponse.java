package com.japes.orderservice.dto.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.japes.orderservice.enums.PaymentMethod;
import com.japes.orderservice.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long id;

    private String paymentReference;

    private String orderNumber;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}