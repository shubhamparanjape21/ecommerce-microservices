package com.japes.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.japes.orderservice.dto.client.ProductVariantResponse;

@FeignClient(name = "product-service")
public interface ProductClient {
	@GetMapping("/api/v1/product-variants/sku/{skuCode}")
	public ProductVariantResponse getProductVariantBySkuCode(@PathVariable("skuCode") String skuCode);
}
