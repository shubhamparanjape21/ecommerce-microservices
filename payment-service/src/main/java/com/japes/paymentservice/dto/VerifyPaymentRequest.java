package com.japes.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Razorpay payment verification request")
public class VerifyPaymentRequest {
	@NotBlank(message = "Razorpay order ID is required")
    @Schema(
        description = "Razorpay order ID",
        example = "order_TOsX8arWrkISSI"
    )
    private String razorpayOrderId;

    @NotBlank(message = "Razorpay payment ID is required")
    @Schema(
        description = "Razorpay payment ID",
        example = "pay_T0tnWGjnOqNNg3"
    )
    private String razorpayPaymentId;

    @NotBlank(message = "Razorpay signature is required")
    @Schema(
        description = "Razorpay payment signature",
        example = "a1b2c3d4..."
    )
    private String razorpaySignature;
}
