package com.japes.paymentservice.dto;

import java.math.BigDecimal;

import com.japes.paymentservice.enums.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
	@Schema(description = "Order number", example = "ORD-7DAB5158")
    @NotBlank(message = "Order number is required")
	private String orderNumber;
	
	@Schema(description = "Payment amount", example = "5000.00")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
	private BigDecimal amount;
	
	@Schema(
            description = "Payment method",
            example = "UPI",
            allowableValues = {
                    "UPI",
                    "CREDIT_CARD",
                    "DEBIT_CARD",
                    "NET_BANKING",
                    "WALLET"
            })
    @NotNull(message = "Payment method is required")
	private PaymentMethod paymentMethod;
}
