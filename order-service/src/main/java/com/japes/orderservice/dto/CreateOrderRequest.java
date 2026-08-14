package com.japes.orderservice.dto;

import java.util.List;

import com.japes.orderservice.enums.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "ID of the user placing the order", example = "1")
	@NotNull(message = "User ID is required")
	private Long userId;
	
	@Schema(description = "List of order items")
	@NotEmpty(message = "Order must contain at least one item")
    @Valid
	private List<OrderItemRequest> items;
	@NotNull(message = "Payment method is required")
	private PaymentMethod paymentMethod;
}
