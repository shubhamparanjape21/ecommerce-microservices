package com.japes.productservice.service;

import com.japes.productservice.dto.ProductRequest;
import com.japes.productservice.dto.ProductResponse;

public interface ProductService {
	ProductResponse createProduct(ProductRequest productRequest);
}
