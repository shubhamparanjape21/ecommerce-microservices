package com.japes.productservice.dto.productvariant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Product Variant Attribute")
public class VariantAttributeRequest {
	@Schema(
            description = "Attribute name",
            example = "Color"
    )
    @NotBlank(message = "Attribute name is required")
    @Size(max = 100, message = "Attribute name cannot exceed 100 characters")
    private String attributeName;

    @Schema(
            description = "Attribute value",
            example = "Black"
    )
    @NotBlank(message = "Attribute value is required")
    @Size(max = 255, message = "Attribute value cannot exceed 255 characters")
    private String attributeValue;
}
