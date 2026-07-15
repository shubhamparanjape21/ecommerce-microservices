package com.japes.productservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.CreateProductResponse;
import com.japes.productservice.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	
	@PostMapping
	public ResponseEntity<CreateProductResponse> saveproduct(@RequestBody @Valid CreateProductRequest productRequest) {
		CreateProductResponse createdProduct = productService.createProduct(productRequest);
		return new ResponseEntity<CreateProductResponse>(createdProduct, HttpStatus.CREATED);
	}
}
