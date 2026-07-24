package com.japes.orderservice.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {
	@Schema(description = "Product SKU", example = "AIRPODS2USB")
	@NotBlank(message = "SKU Code is required")
	private String skuCode;
	
	@Schema(description = "Quantity to order", example = "2")
	@NotNull(message = "Quantity is required")
	@Positive(message = "Quantity must be greater than zero")
	private Integer quantity;
}
