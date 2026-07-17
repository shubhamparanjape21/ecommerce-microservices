package com.japes.productservice.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductPageResponse;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.dto.UpdateProductRequest;
import com.japes.productservice.entity.Product;
import com.japes.productservice.exception.ProductAlreadyExistsException;
import com.japes.productservice.exception.ProductNotFoundException;
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
	public ProductResponse saveProduct(CreateProductRequest productRequest) {
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
	public ProductPageResponse getProductList(int page, int size, String sortBy, String direction) {
		log.info("Fetching products - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);
		Sort sort = direction.equalsIgnoreCase("asc")
						? Sort.by(sortBy).ascending()
						: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Product> productPage = productRepository.findAll(pageable);
		log.debug("Retrieved {} products from database", productPage.getNumberOfElements());
		log.debug("Mapping Product entities to ProductResponse DTOs");
		List<ProductResponse> responses = productPage.getContent()
					.stream()
					.map(product ->
							modelMapper.map(product, ProductResponse.class))
					.toList();
		ProductPageResponse response = new ProductPageResponse(responses,productPage.getNumber(),productPage.getTotalPages(),productPage.getTotalElements(),productPage.getSize(),productPage.isFirst(),productPage.isLast());
		log.info(
			    "Successfully fetched {} products (page {} of {})",
			    productPage.getNumberOfElements(),
			    productPage.getNumber(),
			    productPage.getTotalPages()
			);
		return response;
	}

	@Override
	public ProductResponse getProductById(Long id) {
		log.info("Received request to fetch product with ID {}", id);
		log.debug("Checking whether product with ID {} exists", id);
		Product product = productRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Product not found with ID {}", id);
					return new ProductNotFoundException("Product with ID " + id + " not found");
				});
		log.debug("Mapping Product entity to ProductResponse");
		ProductResponse response = modelMapper.map(product, ProductResponse.class);
		log.info("Successfully fetched product with ID {}", id);
		return response;
	}

	@Override
	public ProductResponse updateProduct(Long id, UpdateProductRequest updateProductRequest) {
		log.info("Received request to update product with ID {}", id);
		log.debug("Checking whether product with ID {} exists", id);
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Product not found with ID {}", id);
					return new ProductNotFoundException("Product with ID " + id + " not found");
				});
		if(!existingProduct.getSkuCode().equals(updateProductRequest.getSkuCode()) && productRepository.existsBySkuCode(updateProductRequest.getSkuCode())) {
			log.warn("Duplicate SKU {} provided for update ", updateProductRequest.getSkuCode());
			throw new ProductAlreadyExistsException("Another product with SKU " + updateProductRequest.getSkuCode() + " already exists");
		}
		log.debug("Mapping UpdateProductRequest to existing product entity");
		modelMapper.map(updateProductRequest, existingProduct);
		log.debug("Saving updated product to database");
		Product updatedProduct = productRepository.save(existingProduct);
		log.debug("Mapping Product entity to ProductResponse");
		ProductResponse response = modelMapper.map(updatedProduct, ProductResponse.class);
		log.info("Successfully updated product with ID {}", response.getId());
		return response;
	}

	@Override
	public void deleteProduct(Long id) {
		log.info("Received request to delete product with ID {}", id);
		log.debug("Checking whether product with ID {} exists", id);
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Product not found with ID {}", id);
					return new ProductNotFoundException("Product with ID " + id + " not found");
				});
		log.debug("Deleting product with ID {} from database", id);
		productRepository.deleteById(id);
		log.info("Product deleted successfully");
	}
}
