package com.japes.orderservice.dto.client;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductVariantResponse {
	private String skuCode;
	private BigDecimal price;
	private boolean active;
}
