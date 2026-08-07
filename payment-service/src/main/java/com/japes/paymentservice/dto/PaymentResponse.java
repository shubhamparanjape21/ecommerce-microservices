package com.japes.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.japes.paymentservice.enums.PaymentMethod;
import com.japes.paymentservice.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
	@Schema(description = "Payment ID", example = "1")
    private Long id;

    @Schema(description = "Payment reference", example = "PAY-A8F3D92C")
    private String paymentReference;

    @Schema(description = "Order number", example = "ORD-7DAB5158")
    private String orderNumber;

    @Schema(description = "Amount paid", example = "5000.00")
    private BigDecimal amount;

    @Schema(description = "Payment method", example = "UPI")
    private PaymentMethod paymentMethod;

    @Schema(description = "Payment status", example = "SUCCESS")
    private PaymentStatus paymentStatus;

    @Schema(description = "Gateway transaction ID", example = "TXN-98374432")
    private String transactionId;

    @Schema(description = "Payment creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Payment last update timestamp")
    private LocalDateTime updatedAt;
}
