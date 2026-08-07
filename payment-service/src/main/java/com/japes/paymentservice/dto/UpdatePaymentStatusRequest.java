package com.japes.paymentservice.dto;

import com.japes.paymentservice.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePaymentStatusRequest {
	@Schema(
            description = "Payment status",
            example = "SUCCESS",
            allowableValues = {
                    "PENDING",
                    "SUCCESS",
                    "FAILED",
                    "REFUNDED"
            })
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    @Schema(description = "Gateway transaction ID", example = "TXN-98374432")
    private String transactionId;
}
