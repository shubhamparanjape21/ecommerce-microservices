package com.japes.orderservice.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderPaidEvent(String orderNumber, String email, List<OrderPaidItem> items, BigDecimal totalAmount,
		String paymentStatus) {
	public record OrderPaidItem(String skuCode, Integer quantity, BigDecimal unitPrice, BigDecimal subTotal) {

	}
}
