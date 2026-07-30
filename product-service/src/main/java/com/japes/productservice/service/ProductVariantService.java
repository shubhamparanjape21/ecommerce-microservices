package com.japes.productservice.service;

import java.util.List;

import com.japes.productservice.dto.productvariant.CreateProductVariantRequest;
import com.japes.productservice.dto.productvariant.ProductVariantResponse;

public interface ProductVariantService {
	ProductVariantResponse createVariant(CreateProductVariantRequest request);
	ProductVariantResponse getVariantById(Long id);
	ProductVariantResponse getVariantBySkuCode(String skuCode);
	List<ProductVariantResponse> getVariantsByProductId(Long productId);
}
