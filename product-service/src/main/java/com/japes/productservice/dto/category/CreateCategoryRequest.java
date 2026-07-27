package com.japes.productservice.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for creating a new category")
public class CreateCategoryRequest {
	@Schema(
            description = "Category name",
            example = "Electronics"
    )
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
	private String name;
	@Schema(
            description = "Category description",
            example = "Electronic gadgets and accessories"
    )
    @NotBlank(message = "Category description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
	private String description;
}
