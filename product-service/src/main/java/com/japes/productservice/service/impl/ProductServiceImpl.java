package com.japes.productservice.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.entity.Product;
import com.japes.productservice.exception.ProductAlreadyExistsException;
import com.japes.productservice.repository.ProductRepository;
import com.japes.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;

	@Override
	public ProductResponse createProduct(CreateProductRequest productRequest) {
		log.info("Creating product with SKU {}", productRequest.getSkuCode());
		log.debug("Checking if product with SKU {} already exists", productRequest.getSkuCode());
		if(productRepository.existsBySkuCode(productRequest.getSkuCode())) {
			log.warn("Duplicate product creation attempted for SKU {}", productRequest.getSkuCode());
			throw new ProductAlreadyExistsException("Product with SKU " + productRequest.getSkuCode() + " already exists");
		}
		log.debug("Mapping CreateProductRequest to Product entity");
		Product product = modelMapper.map(productRequest, Product.class);
		log.debug("Saving product to database");
		Product savedProduct = productRepository.save(product);
		log.info("Product created successfully with ID {}", savedProduct.getId());
		return modelMapper.map(savedProduct, ProductResponse.class);
	}

	@Override
	public List<ProductResponse> getAllProducts() {
		log.info("Fetching all products");
		List<Product> products = productRepository.findAll();
		log.debug("Retrieved {} products from database", products.size());
		log.debug("Mapping Product entities to ProductResponse DTOs");
		List<ProductResponse> response = products.stream()
					.map(product -> modelMapper.map(product, ProductResponse.class))
					.toList();
		log.info("Successfully fetched {} products", response.size());
		return response;
	}

}
