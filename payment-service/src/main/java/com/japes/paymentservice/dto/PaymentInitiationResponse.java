package com.japes.paymentservice.dto;

import java.math.BigDecimal;

import com.japes.paymentservice.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInitiationResponse {
	private String paymentReference;
	private String stripePaymentIntentId;
	private String clientSecret;
	private BigDecimal amount;
	private PaymentStatus paymentStatus;
}
