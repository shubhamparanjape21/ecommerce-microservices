package com.japes.paymentservice.service;

import com.japes.paymentservice.dto.CreatePaymentRequest;
import com.japes.paymentservice.dto.PaymentResponse;

public interface PaymentService {
	public PaymentResponse createPayment(CreatePaymentRequest request);
	public PaymentResponse getPaymentByReference(String paymentReference);
	public PaymentResponse getPaymentByOrderNumber(String orderNumber);
}
