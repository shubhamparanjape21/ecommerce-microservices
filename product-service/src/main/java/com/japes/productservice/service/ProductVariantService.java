package com.japes.productservice.service;

import java.util.List;

import com.japes.productservice.dto.productvariant.CreateProductVariantRequest;
import com.japes.productservice.dto.productvariant.ProductVariantResponse;
import com.japes.productservice.dto.productvariant.UpdateProductVariantRequest;

public interface ProductVariantService {
	ProductVariantResponse createProductVariant(CreateProductVariantRequest request);
	ProductVariantResponse getProductVariantById(Long id);
	ProductVariantResponse getProductVariantBySkuCode(String skuCode);
	List<ProductVariantResponse> getProductVariantsByProductId(Long productId);
	ProductVariantResponse updateProductVariant(Long id, UpdateProductVariantRequest request);
	void deleteProductVariant(Long id);
}
