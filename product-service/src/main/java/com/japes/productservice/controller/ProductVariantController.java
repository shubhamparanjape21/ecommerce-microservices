package com.japes.productservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.japes.productservice.dto.productvariant.CreateProductVariantRequest;
import com.japes.productservice.dto.productvariant.ProductVariantResponse;
import com.japes.productservice.service.ProductVariantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {
	private final ProductVariantService productVariantService;
	
	@PostMapping
	@Operation(summary = "Create Product Variant")
    @ApiResponse(responseCode = "201", description = "Product Variant created successfully")
	public ResponseEntity<ProductVariantResponse> createVariant(@Valid @RequestBody CreateProductVariantRequest request) {
		log.info("Received request to create product variant for product ID {}", request.getProductId());
		ProductVariantResponse response = productVariantService.createVariant(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/{id}")
    @Operation(summary = "Get Product Variant by ID")
    @ApiResponse(responseCode = "200", description = "Product Variant fetched successfully")
    public ResponseEntity<ProductVariantResponse> getVariantById(
            @PathVariable Long id) {

        log.info("Received request to fetch product variant with ID {}", id);
        ProductVariantResponse response = productVariantService.getVariantById(id);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/sku/{skuCode}")
    @Operation(summary = "Get Product Variant by SKU Code")
    @ApiResponse(responseCode = "200", description = "Product Variant fetched successfully")
    public ResponseEntity<ProductVariantResponse> getVariantBySkuCode(
            @PathVariable String skuCode) {

        log.info("Received request to fetch product variant with SKU {}", skuCode);
        ProductVariantResponse response = productVariantService.getVariantBySkuCode(skuCode);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/product/{productId}")
    @Operation(summary = "Get all variants for a Product")
    @ApiResponse(responseCode = "200", description = "Product Variants fetched successfully")
    public ResponseEntity<List<ProductVariantResponse>> getVariantsByProductId(
            @PathVariable Long productId) {

        log.info("Received request to fetch variants for product {}", productId);
        List<ProductVariantResponse> response = productVariantService.getVariantsByProductId(productId);
        return ResponseEntity.ok(response);
    }
}
