package com.japes.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing inventory details")
public class InventoryResponse {
	@Schema(
	        description = "Unique identifier of the inventory record",
	        example = "1"
	    )
	private Long id;
	@Schema(
	        description = "Unique Stock Keeping Unit (SKU) of the product",
	        example = "IPH15PRO256"
	    )
	private String skuCode;
	@Schema(
	        description = "Available quantity of the product in inventory",
	        example = "25",
	        minimum = "0"
	    )
	private Integer quantity;
}
