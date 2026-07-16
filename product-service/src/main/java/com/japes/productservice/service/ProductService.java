package com.japes.productservice.service;

import java.util.List;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductResponse;

public interface ProductService {
	ProductResponse createProduct(CreateProductRequest productRequest);
	List<ProductResponse> getAllProducts();
}
