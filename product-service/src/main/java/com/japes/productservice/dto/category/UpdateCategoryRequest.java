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
@Schema(description = "Request payload for updating a category")
public class UpdateCategoryRequest {
	@Schema(
            description = "Category name",
            example = "Electronics"
    )
    @NotBlank(message = "Category name is required")
    @Size(max = 100)
    private String name;

    @Schema(
            description = "Category description",
            example = "Electronic gadgets and accessories"
    )
    @NotBlank(message = "Category description is required")
    @Size(max = 1000)
    private String description;
}
