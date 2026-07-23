package com.japes.orderservice.service;

import com.japes.orderservice.dto.CreateOrderRequest;
import com.japes.orderservice.dto.OrderResponse;

public interface OrderService {
	OrderResponse placeOrder(CreateOrderRequest request);
	OrderResponse getOrderByOrderNumber(String orderNumber);
}
