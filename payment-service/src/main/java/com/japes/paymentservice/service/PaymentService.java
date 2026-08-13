package com.japes.paymentservice.service;

import com.japes.paymentservice.dto.CreatePaymentRequest;
import com.japes.paymentservice.dto.PaymentInitiationResponse;
import com.japes.paymentservice.dto.PaymentPageResponse;
import com.japes.paymentservice.dto.PaymentResponse;
import com.japes.paymentservice.dto.UpdatePaymentStatusRequest;
import com.japes.paymentservice.dto.VerifyPaymentRequest;
import com.japes.paymentservice.enums.PaymentStatus;

public interface PaymentService {
	public PaymentResponse createPayment(CreatePaymentRequest request);
	public PaymentResponse getPaymentByReference(String paymentReference);
	public PaymentResponse getPaymentByOrderNumber(String orderNumber);
	public PaymentResponse updatePaymentStatus(String paymentReference, UpdatePaymentStatusRequest request);
	public PaymentResponse refundPayment(String paymentReference);
	public PaymentPageResponse getPaymentsByStatus(PaymentStatus status, int page, int pageSize, String sortBy, String direction);
	public PaymentInitiationResponse initiatePayment(String paymentReference);
	public PaymentResponse verifyPayment(VerifyPaymentRequest request);
	public void handleWebhook(String payload, String signature);
}
