package com.japes.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "order-service")
public interface OrderClient {
	@PutMapping("/api/v1/orders/payment-pending/{orderNumber}")
	void markPaymentPending(@PathVariable String orderNumber);
	
	@PutMapping("/api/v1/orders/payment-success/{orderNumber}")
	void handleSuccessfulPayment(@PathVariable String orderNumber);
	
	@PutMapping("/api/v1/orders/payment-failed/{orderNumber}")
	void handleFailedPayment(@PathVariable String orderNumber);
	
}
