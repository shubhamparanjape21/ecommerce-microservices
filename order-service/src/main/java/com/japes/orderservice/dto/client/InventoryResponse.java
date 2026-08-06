package com.japes.orderservice.dto.client;

import lombok.Data;

@Data
public class InventoryResponse {
	private String skuCode;
	private Integer quantity;
}
