package com.japes.productservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductPageResponse;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.dto.UpdateProductRequest;
import com.japes.productservice.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products including create, retrieve, update, delete, search, pagination and sorting.")
public class ProductController {
	private final ProductService productService;
	
	@Operation(
		    summary = "Create a new product",
		    description = "Creates a new product after validating the request and ensuring the SKU is unique."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "201", description = "Product created successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid product request", content = @Content),
		    @ApiResponse(responseCode = "409", description = "Product with the given SKU already exists", content = @Content)
		})
	@PostMapping
	public ResponseEntity<ProductResponse> saveproduct(@RequestBody @Valid CreateProductRequest productRequest) {
		ProductResponse createdProduct = productService.saveProduct(productRequest);
		return new ResponseEntity<ProductResponse>(createdProduct, HttpStatus.CREATED);
	}
	
	@Operation(
		    summary = "Get all products",
		    description = "Returns a paginated and sortable list of products."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Products fetched successfully")
		})
	@GetMapping
	public ResponseEntity<ProductPageResponse> getProductList(
			@Parameter(description = "Page number (starts from 0)", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Number of records per page", example = "10")
			@RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Field used for sorting", example = "price")
			@RequestParam(defaultValue = "id") String sortBy,
			@Parameter(description = "Sorting direction: asc or desc", example = "asc")
			@RequestParam(defaultValue = "asc") String direction) {
		log.info("Received request to fetch products - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);
		ProductPageResponse response = productService.getProductList(page, size, sortBy, direction);
		return ResponseEntity.ok(response);
	}
	
	@Operation(
		    summary = "Get product by ID",
		    description = "Fetches a product using its unique identifier."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Product found"),
		    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
		})
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@Parameter(description = "Unique product ID", example = "1") @PathVariable Long id) {
		ProductResponse response = productService.getProductById(id);
		return new ResponseEntity<ProductResponse>(response, HttpStatus.OK);
	}
	
	@Operation(
		    summary = "Update product",
		    description = "Updates an existing product using its ID."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Product updated successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid product request", content = @Content),
		    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
		    @ApiResponse(responseCode = "409", description = "Duplicate SKU", content = @Content)
		})
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest updateProductRequest) {
		ProductResponse response = productService.updateProduct(id, updateProductRequest);
		return new ResponseEntity<ProductResponse>(response, HttpStatus.OK);
	}
	
	@Operation(
		    summary = "Delete product",
		    description = "Deletes a product using its ID."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
		    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
		})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@Parameter(description = "Unique product ID", example = "1") @PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
}
