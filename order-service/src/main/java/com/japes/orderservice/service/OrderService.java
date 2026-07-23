package com.japes.orderservice.service;

import com.japes.orderservice.dto.CreateOrderRequest;
import com.japes.orderservice.dto.OrderPageResponse;
import com.japes.orderservice.dto.OrderResponse;
import com.japes.orderservice.dto.UpdateOrderStatusRequest;

public interface OrderService {
	OrderResponse placeOrder(CreateOrderRequest request);
	OrderResponse getOrderByOrderNumber(String orderNumber);
	OrderPageResponse getOrdersByUserId(Long userId, int page, int size, String sortBy, String direction);
	OrderResponse updateOrderStatus(String orderNumber, UpdateOrderStatusRequest request);
}
