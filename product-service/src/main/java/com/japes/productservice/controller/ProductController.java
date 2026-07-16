package com.japes.productservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.dto.UpdateProductRequest;
import com.japes.productservice.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
	private final ProductService productService;
	
	@PostMapping
	public ResponseEntity<ProductResponse> saveproduct(@RequestBody @Valid CreateProductRequest productRequest) {
		ProductResponse createdProduct = productService.saveProduct(productRequest);
		return new ResponseEntity<ProductResponse>(createdProduct, HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<List<ProductResponse>> fetchAllProducts() {
		log.info("Received request to fetch all products");
		List<ProductResponse> list = productService.getProductList();
		return new ResponseEntity<List<ProductResponse>>(list, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable long id) {
		ProductResponse response = productService.getProductById(id);
		return new ResponseEntity<ProductResponse>(response, HttpStatus.OK);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest updateProductRequest) {
		ProductResponse response = productService.updateProduct(id, updateProductRequest);
		return new ResponseEntity<ProductResponse>(response, HttpStatus.OK);
	}
}
