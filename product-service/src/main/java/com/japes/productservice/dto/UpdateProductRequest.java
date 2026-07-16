package com.japes.productservice.dto;

import java.math.BigDecimal;

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
public class UpdateProductRequest {
	@NotBlank(message = "skuCode is required!")
	private String skuCode;
	@NotBlank(message = "name is required!")
	private String name;
	@NotBlank(message = "description is required!")
	@Size(min = 10, message = "Description is too short")
	private String description;
	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than 0")
	private BigDecimal price;
}
