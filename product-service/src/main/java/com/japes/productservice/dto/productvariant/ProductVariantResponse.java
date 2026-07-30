package com.japes.productservice.dto.productvariant;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Product Variant Response")
public class ProductVariantResponse {
	private Long id;
    private Long productId;
    private String productName;
    private String skuCode;
    private BigDecimal price;
    private Boolean active;
    private List<VariantAttributeResponse> attributes;
}
