package com.japes.productservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for updating a product")
public class UpdateProductRequest {
	
	@Schema(
		    description = "Unique SKU code of the product",
		    example = "IPH15PRO256"
		)
	@NotBlank(message = "skuCode is required!")
	private String skuCode;
	
	@Schema(
		    description = "Product name",
		    example = "iPhone 15 Pro"
		)
	@NotBlank(message = "name is required!")
	private String name;
	
	@Schema(
		    description = "Product description",
		    example = "Apple iPhone 15 Pro with 256GB storage."
		)
	@NotBlank(message = "description is required!")
	@Size(min = 10, message = "Description is too short")
	private String description;
	
	@Schema(
		    description = "Product price",
		    example = "129999.00"
		)
	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than 0")
	private BigDecimal price;
}
