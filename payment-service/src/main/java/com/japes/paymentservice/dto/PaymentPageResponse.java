package com.japes.paymentservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPageResponse {
	@Schema(description = "Payments in current page")
    private List<PaymentResponse> payments;

    @Schema(description = "Current page number", example = "0")
    private int currentPage;

    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;

    @Schema(description = "Total number of records", example = "50")
    private long totalElements;

    @Schema(description = "Page size", example = "10")
    private int pageSize;

    @Schema(description = "Whether this is the first page", example = "true")
    private boolean first;

    @Schema(description = "Whether this is the last page", example = "false")
    private boolean last;
}
