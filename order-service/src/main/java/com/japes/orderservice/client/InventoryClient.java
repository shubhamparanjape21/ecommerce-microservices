package com.japes.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.japes.orderservice.dto.client.InventoryResponse;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
	@GetMapping("/api/v1/inventory/sku/{skuCode}")
	public InventoryResponse getInventoryBySkuCode(@PathVariable("skuCode") String skuCode);
}