package com.japes.productservice.service;

import com.japes.productservice.dto.product.CreateProductRequest;
import com.japes.productservice.dto.product.ProductPageResponse;
import com.japes.productservice.dto.product.ProductResponse;
import com.japes.productservice.dto.product.UpdateProductRequest;

public interface ProductService {
	ProductResponse saveProduct(CreateProductRequest productRequest);
	ProductPageResponse getProductList(int page, int size, String sortBy, String direction);
	ProductResponse getProductById(Long id);
	ProductResponse updateProduct(Long id, UpdateProductRequest productRequest);
	void deleteProduct(Long id);
}
