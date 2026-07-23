package com.japes.orderservice.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
	@NotNull(message = "User ID is required")
	private Long userId;
	@NotEmpty(message = "Order must contain at least one item")
    @Valid
	private List<OrderItemRequest> items;
}
