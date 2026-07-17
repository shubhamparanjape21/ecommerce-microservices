package com.japes.productservice.service;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductPageResponse;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.dto.UpdateProductRequest;

public interface ProductService {
	ProductResponse saveProduct(CreateProductRequest productRequest);
	ProductPageResponse getProductList(int page, int size, String sortBy, String direction);
	ProductResponse getProductById(Long id);
	ProductResponse updateProduct(Long id, UpdateProductRequest productRequest);
	void deleteProduct(Long id);
}
