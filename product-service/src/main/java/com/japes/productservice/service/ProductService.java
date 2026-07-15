package com.japes.productservice.service;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.CreateProductResponse;

public interface ProductService {
	CreateProductResponse createProduct(CreateProductRequest productRequest);
}
