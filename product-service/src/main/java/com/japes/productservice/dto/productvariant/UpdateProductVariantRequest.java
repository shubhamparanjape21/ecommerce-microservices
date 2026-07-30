package com.japes.productservice.dto.productvariant;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request to update a product variant")
public class UpdateProductVariantRequest {
	@Schema(example = "IP16-BLK-128")
    @NotBlank(message = "SKU Code is required")
    private String skuCode;

    @Schema(example = "79999.00")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @Schema(description = "Variant status")
    @NotNull(message = "Active status is required")
    private Boolean active;

    @Schema(description = "Variant attributes")
    @NotEmpty(message = "At least one attribute is required")
    @Valid
    private List<VariantAttributeRequest> attributes;
}
