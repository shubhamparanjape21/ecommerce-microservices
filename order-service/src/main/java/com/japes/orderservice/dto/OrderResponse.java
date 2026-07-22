package com.japes.orderservice.dto;

import java.util.List;

import com.japes.orderservice.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	private Long id;
	private String orderNumber;
	private Long userId;
	private OrderStatus status;
	private List<OrderItemResponse> items;
}
