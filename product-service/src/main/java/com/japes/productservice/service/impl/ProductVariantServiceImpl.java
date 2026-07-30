package com.japes.productservice.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.japes.productservice.dto.productvariant.CreateProductVariantRequest;
import com.japes.productservice.dto.productvariant.ProductVariantResponse;
import com.japes.productservice.dto.productvariant.UpdateProductVariantRequest;
import com.japes.productservice.dto.productvariant.VariantAttributeResponse;
import com.japes.productservice.entity.Product;
import com.japes.productservice.entity.ProductVariant;
import com.japes.productservice.entity.VariantAttribute;
import com.japes.productservice.exception.product.ProductNotFoundException;
import com.japes.productservice.exception.productvariant.ProductVariantAlreadyExistsException;
import com.japes.productservice.exception.productvariant.ProductVariantNotFoundException;
import com.japes.productservice.repository.ProductRepository;
import com.japes.productservice.repository.ProductVariantRepository;
import com.japes.productservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
	
	private final ProductVariantRepository productVariantRepository;
	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;

	@Override
	public ProductVariantResponse createProductVariant(CreateProductVariantRequest request) {
		log.info("Creating product variant with SKU {}", request.getSkuCode());

		log.debug("Checking whether product with ID {} exists", request.getProductId());
		
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> {
					log.warn("Product not found with ID {}",
		                    request.getProductId());
					return new ProductNotFoundException("Product with ID " + request.getProductId() + " not found");
				});
		log.debug("Checking whether SKU {} already exists", request.getSkuCode());
		
		if(productVariantRepository.existsBySkuCode(request.getSkuCode())) {
			log.warn("Duplicate SKU {} detected", request.getSkuCode());
			throw new ProductVariantAlreadyExistsException("Product variant with SKU " + request.getSkuCode() + " already exists");
		}
		// Create Variant entity
		ProductVariant variant = new ProductVariant();
		variant.setProduct(product);
		variant.setSkuCode(request.getSkuCode());
		variant.setPrice(request.getPrice());
		variant.setActive(true);
		
		// Map attributes
		List<VariantAttribute> attributes = request.getAttributes()
				.stream()
				.map(attributeRequest -> {
					VariantAttribute attribute = new VariantAttribute();
					attribute.setAttributeName(attributeRequest.getAttributeName());
					attribute.setAttributeValue(attributeRequest.getAttributeValue());
					attribute.setProductVariant(variant); // Attach variant
					return attribute;
				})
				.toList();
		
		// Attach attributes
		variant.setAttributes(attributes);
		
		// save
		log.debug("Saving product variant");
		ProductVariant savedVariant = productVariantRepository.save(variant);
		
		log.info("Successfully created product variant with SKU {} for product ID {}", savedVariant.getSkuCode(), savedVariant.getProduct().getId());
		return mapToProductVariantResponse(savedVariant);
	}

	@Override
	public ProductVariantResponse getProductVariantById(Long id) {
		log.info("Fetching product variant with ID {}", id);
		ProductVariant variant = productVariantRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Product variant not found with ID {}", id);
					return new ProductVariantNotFoundException("Product Variant with ID " + id + " not found");
				});
		log.info("Successfully fetched product variant with ID {}", id);
		return mapToProductVariantResponse(variant);
	}

	@Override
	public ProductVariantResponse getProductVariantBySkuCode(String skuCode) {
		log.info("Fetching product variant with SKU {}", skuCode);
		ProductVariant variant = productVariantRepository.findBySkuCode(skuCode)
				.orElseThrow(() -> {
					log.warn("Product variant not found with SKU {}", skuCode);
	                return new ProductVariantNotFoundException("Product Variant with SKU " + skuCode + " not found");
				});
		log.info("Successfully fetched product variant {}", skuCode);
	    return mapToProductVariantResponse(variant);
	}

	@Override
	public List<ProductVariantResponse> getProductVariantsByProductId(Long productId) {
		log.info("Fetching variants for product {}", productId);
	    productRepository.findById(productId)
	            .orElseThrow(() -> {
	                log.warn("Product not found with ID {}", productId);
	                return new ProductNotFoundException(
	                        "Product with ID " + productId + " not found");
	            });
	    List<ProductVariant> variants =
	            productVariantRepository.findByProductId(productId);
	    log.info("Found {} variants", variants.size());
	    return variants.stream()
	            .map(this::mapToProductVariantResponse)
	            .toList();
	}
	
	@Override
	public ProductVariantResponse updateProductVariant(Long id, UpdateProductVariantRequest request) {
		log.info("Updating product variant with ID {}", id);
	    log.debug("Checking whether product variant with ID {} exists", id);
	    
	    ProductVariant existingVariant = productVariantRepository.findById(id)
	            .orElseThrow(() -> {
	                log.warn("Product variant not found with ID {}", id);
	                return new ProductVariantNotFoundException(
	                        "Product variant with ID " + id + " not found");
	            });

	    if (!existingVariant.getSkuCode().equals(request.getSkuCode()) && productVariantRepository.existsBySkuCode(request.getSkuCode())) {
	        log.warn("Duplicate SKU {} provided for update", request.getSkuCode());
	        throw new ProductVariantAlreadyExistsException(
	                "Another product variant with SKU " + request.getSkuCode() + " already exists");
	    }
	    log.debug("Mapping UpdateProductVariantRequest to existing ProductVariant");

	    modelMapper.map(request, existingVariant);

	    log.debug("Saving updated product variant");

	    ProductVariant updatedVariant = productVariantRepository.save(existingVariant);

	    log.info("Successfully updated product variant with ID {}", updatedVariant.getId());

	    return mapToProductVariantResponse(updatedVariant);
	}
	
	@Override
	public void deleteProductVariant(Long id) {
		log.info("Deleting product variant with ID {}", id);

	    log.debug("Checking whether product variant with ID {} exists", id);

	    ProductVariant existingVariant = productVariantRepository.findById(id)
	            .orElseThrow(() -> {
	                log.warn("Product variant not found with ID {}", id);
	                return new ProductVariantNotFoundException(
	                        "Product variant with ID " + id + " not found");
	            });

	    log.debug("Deleting product variant with ID {} from database", id);

	    productVariantRepository.delete(existingVariant);

	    log.info("Successfully deleted product variant with ID {}", id);	
	}
	
	private ProductVariantResponse mapToProductVariantResponse(ProductVariant variant) {
		ProductVariantResponse response = new ProductVariantResponse();
		
		response.setId(variant.getId());
		response.setProductId(variant.getProduct().getId());
		response.setProductName(variant.getProduct().getName());
		response.setSkuCode(variant.getSkuCode());
		response.setPrice(variant.getPrice());
		response.setActive(variant.getActive());
		response.setAttributes(variant.getAttributes()
				.stream()
				.map(this::mapToVariantAttributeResponse)
				.toList()
		);
		return response;
	}
	
	private VariantAttributeResponse mapToVariantAttributeResponse(VariantAttribute attribute) {
		VariantAttributeResponse response = new VariantAttributeResponse();
		
		response.setId(attribute.getId());
		response.setAttributeName(attribute.getAttributeName());
		response.setAttributeValue(attribute.getAttributeValue());
		
		return response;
	}
}
