package com.japes.paymentservice.service;

import com.japes.paymentservice.dto.CreatePaymentRequest;
import com.japes.paymentservice.dto.PaymentResponse;
import com.japes.paymentservice.dto.UpdatePaymentStatusRequest;

public interface PaymentService {
	public PaymentResponse createPayment(CreatePaymentRequest request);
	public PaymentResponse getPaymentByReference(String paymentReference);
	public PaymentResponse getPaymentByOrderNumber(String orderNumber);
	public PaymentResponse updatePaymentStatus(String paymentReference, UpdatePaymentStatusRequest request);
	public PaymentResponse refundPayment(String paymentReference);
}
