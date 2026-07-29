package com.japes.productservice.dto.product;

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
	@Schema(description = "Product name", example = "iPhone 15 Pro")
	private String name;
	@Schema(description = "Detailed product description", example = "Apple iPhone 15 Pro with 256GB storage and A17 Pro chip.")
	private String description;
	@Schema(description = "Product brand", example = "Apple")
	private String brand;
	@Schema(description = "Product image URL", example = "https://cdn.japes.com/images/iphone16.jpg")
	private String imageUrl;
	@Schema(description = "Whether the product is active", example = "true")
	private Boolean active;
	@Schema(description = "Category identifier", example = "1")
	private Long categoryId;
	@Schema(description = "Category name", example = "Electronics")
	private String categoryName;
}
