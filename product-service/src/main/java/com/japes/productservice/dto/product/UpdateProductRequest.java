package com.japes.productservice.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
		    description = "Product name",
		    example = "iPhone 15 Pro"
		)
	@NotBlank(message = "name is required!")
	@Size(max = 255, message = "Product name cannot exceed 255 characters")
	private String name;

	@Schema(
		    description = "Product description",
		    example = "Apple iPhone 15 Pro with 256GB storage."
		)
	@NotBlank(message = "description is required!")
	@Size(min = 10, message = "Description is too short")
	private String description;

	@Schema(
            description = "Product brand",
            example = "Apple"
    )
    @NotBlank(message = "Brand is required")
	private String brand;

	@Schema(
            description = "URL of the product image",
            example = "https://cdn.japes.com/images/iphone16.jpg"
    )
    @NotBlank(message = "Image URL is required")
	private String imageUrl;

	@Schema(
            description = "Category identifier",
            example = "1"
    )
    @NotNull(message = "Category is required")
	private Long categoryId;

	@Schema(
            description = "Product availability status",
            example = "true"
    )
    @NotNull(message = "Product status is required")
	private boolean active;
}
