package com.japes.productservice.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.japes.productservice.dto.ProductRequest;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.entity.Product;
import com.japes.productservice.repository.ProductRepository;
import com.japes.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;

	@Override
	public ProductResponse createProduct(ProductRequest productRequest) {
		Product product = modelMapper.map(productRequest, Product.class);
		Product savedProduct = productRepository.save(product);
		return modelMapper.map(savedProduct, ProductResponse.class);
	}

}
