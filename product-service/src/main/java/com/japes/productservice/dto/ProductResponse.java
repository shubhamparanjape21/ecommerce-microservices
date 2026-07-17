package com.japes.productservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Response containing product details")
public class ProductResponse {
	@Schema(description = "Unique product ID", example = "1")
	private Long id;
	@Schema(description = "Unique Stock Keeping Unit (SKU) code", example = "IPH15PRO256")
	private String skuCode;
	@Schema(description = "Product name", example = "iPhone 15 Pro")
	private String name;
	@Schema(description = "Detailed product description", example = "Apple iPhone 15 Pro with 256GB storage and A17 Pro chip.")
	private String description;
	@Schema(description = "Product price", example = "129999.00")
	private BigDecimal price;
}
