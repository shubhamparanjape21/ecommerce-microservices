package com.japes.paymentservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response returned after initiating a Razorpay payment")
public class PaymentInitiationResponse {
		@Schema(description = "Our internal payment reference", example = "PAY-C284DFB8")
	    private String paymentReference;

	    @Schema(description = "Razorpay order ID", example = "order_R8x123456789")
	    private String razorpayOrderId;

	    @Schema(description = "Payment amount in INR", example = "5000.00")
	    private BigDecimal amount;

	    @Schema(description = "Currency", example = "INR")
	    private String currency;

	    @Schema(description = "Current payment status", example = "PENDING")
	    private String paymentStatus;
	    
	    @Schema(description = "Razorpay public key ID used by the checkout", example = "rzp_test_XXXXXXXXXXXX")
	    private String razorpayKeyId;
}
