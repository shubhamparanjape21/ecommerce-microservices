package com.japes.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for updating an existing inventory record")
public class UpdateInventoryRequest {
    @Schema(
            description = "Updated available quantity of the product in inventory",
            example = "30",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0"
        )
	@NotNull(message = "Quantity is required")
	@PositiveOrZero(message = "Quantity cannot be negative")
	private Integer quantity;
}