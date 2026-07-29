package com.japes.productservice.service.impl;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.japes.productservice.dto.product.CreateProductRequest;
import com.japes.productservice.dto.product.ProductPageResponse;
import com.japes.productservice.dto.product.ProductResponse;
import com.japes.productservice.dto.product.UpdateProductRequest;
import com.japes.productservice.entity.Category;
import com.japes.productservice.entity.Product;
import com.japes.productservice.exception.ProductNotFoundException;
import com.japes.productservice.exception.category.CategoryNotFoundException;
import com.japes.productservice.repository.CategoryRepository;
import com.japes.productservice.repository.ProductRepository;
import com.japes.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;

	@Override
	public ProductResponse saveProduct(CreateProductRequest request) {
		log.info("Received request to create product '{}'", request.getName());
		log.debug("Checking whether category with ID {} exists", request.getCategoryId());
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> {
					log.warn("Category not found with ID {}", request.getCategoryId());
					return new CategoryNotFoundException("Category with ID " + request.getCategoryId() + " not found");
				});
		
		log.debug("Mapping CreateProductRequest to Product entity");
		Product product = modelMapper.map(request, Product.class);
		product.setCategory(category);
		log.debug("Saving product to database");
		Product savedProduct = productRepository.save(product);
		log.info("Product created successfully with ID {}", savedProduct.getId());
		return mapToProductResponse(savedProduct);
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
					.map(this::mapToProductResponse)
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
		ProductResponse response = mapToProductResponse(product);
		log.info("Successfully fetched product with ID {}", id);
		return response;
	}

	@Override
	public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
		log.info("Received request to update product with ID {}", id);
		log.debug("Checking whether product with ID {} exists", id);
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Product not found with ID {}", id);
					return new ProductNotFoundException("Product with ID " + id + " not found");
				});
		log.debug("Checking whether category with ID {} exists", request.getCategoryId());
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> {
					log.warn("Category not found with ID {}", request.getCategoryId());
					return new CategoryNotFoundException("Category with ID " + request.getCategoryId() + " not found");
				});
		log.debug("Updating product details");
		existingProduct.setName(request.getName());
		existingProduct.setDescription(request.getDescription());
		existingProduct.setBrand(request.getBrand());
		existingProduct.setImageUrl(request.getImageUrl());
		existingProduct.setActive(request.isActive());
		existingProduct.setCategory(category);
		log.debug("Saving updated product");
		Product updatedProduct = productRepository.save(existingProduct);
		log.info("Successfully updated product with ID {}", updatedProduct.getId());
		return mapToProductResponse(updatedProduct);
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
		productRepository.delete(existingProduct);
		log.info("Product deleted successfully");
	}
	
	private ProductResponse mapToProductResponse(Product product) {
		ProductResponse response = new ProductResponse();
		
		response.setId(product.getId());
		response.setName(product.getName());
	    response.setDescription(product.getDescription());
	    response.setBrand(product.getBrand());
	    response.setImageUrl(product.getImageUrl());
	    response.setActive(product.isActive());

	    response.setCategoryId(product.getCategory().getId());
	    response.setCategoryName(product.getCategory().getName());
	    
	    return response;
	}
}
