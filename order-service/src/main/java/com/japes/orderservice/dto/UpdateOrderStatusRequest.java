package com.japes.orderservice.dto;

import com.japes.orderservice.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusRequest {
	@Schema(
	        description = "New order status",
	        example = "PROCESSING",
	        allowableValues = {
	            "PENDING",
	            "PAYMENT_PENDING",
	            "PAID",
	            "PROCESSING",
	            "SHIPPED",
	            "DELIVERED",
	            "CANCELLED"
	        }
	    )
	@NotNull(message = "Order status is required")
	private OrderStatus status;
}
