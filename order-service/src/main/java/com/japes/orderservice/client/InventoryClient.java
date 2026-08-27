package com.japes.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.japes.orderservice.config.FeignConfig;
import com.japes.orderservice.dto.client.InventoryResponse;

@FeignClient(name = "inventory-service",configuration = FeignConfig.class)
public interface InventoryClient {
	@GetMapping("/api/v1/inventory/sku/{skuCode}")
	public InventoryResponse getInventoryBySkuCode(@PathVariable("skuCode") String skuCode);
	@PutMapping("/api/v1/inventory/reduce")
	public void reduceInventory(@RequestParam String skuCode, @RequestParam int quantity);
}