package com.japes.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {
	@NotNull(message = "SKU Code is required")
	private String skuCode;
	@NotNull(message = "Quantity is required")
	@Positive(message = "Quantity must be greater than zero")
	private Integer quantity;
}
