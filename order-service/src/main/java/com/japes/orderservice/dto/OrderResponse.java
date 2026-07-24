package com.japes.orderservice.dto;

import java.util.List;

import com.japes.orderservice.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	@Schema(description = "Database ID", example = "1")
	private Long id;
	@Schema(description = "Unique order number", example = "ORD-8A7F3D9B")
	private String orderNumber;
	@Schema(description = "User ID", example = "1")
	private Long userId;
	@Schema(description = "Current order status", example = "PENDING")
	private OrderStatus status;
	@Schema(description = "Ordered items")
	private List<OrderItemResponse> items;
}
