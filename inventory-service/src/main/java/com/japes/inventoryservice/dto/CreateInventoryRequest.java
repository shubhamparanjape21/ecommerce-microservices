package com.japes.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating a new inventory record")
public class CreateInventoryRequest {
	@Schema(
	        description = "Unique Stock Keeping Unit (SKU) of the product",
	        example = "IPH15PRO256",
	        requiredMode = Schema.RequiredMode.REQUIRED
	    )
	@NotBlank(message = "skuCode is required")
	private String skuCode;
	@Schema(
	        description = "Available quantity of the product in inventory",
	        example = "25",
	        requiredMode = Schema.RequiredMode.REQUIRED,
	        minimum = "0"
	    )
	@NotNull(message = "Quantity is required")
	@PositiveOrZero(message = "Quantity cannot be negative")
	private Integer quantity;
}
