package com.japes.productservice.service;

import java.util.List;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.dto.UpdateProductRequest;

public interface ProductService {
	ProductResponse saveProduct(CreateProductRequest productRequest);
	List<ProductResponse> getProductList();
	ProductResponse getProductById(Long id);
	ProductResponse updateProduct(Long id, UpdateProductRequest productRequest);
}
