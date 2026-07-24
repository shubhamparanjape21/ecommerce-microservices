package com.japes.orderservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
	@Schema(description = "Product SKU", example = "AIRPODS2USB")
	private String skuCode;
	@Schema(description = "Quantity ordered", example = "2")
	private Integer quantity;
	@Schema(description = "Price per unit", example = "12999.00")
	private BigDecimal price;
}
