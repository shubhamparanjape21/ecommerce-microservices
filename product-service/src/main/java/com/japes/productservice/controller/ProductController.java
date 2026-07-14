package com.japes.productservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.japes.productservice.dto.ProductRequest;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	
	@PostMapping
	public ResponseEntity<ProductResponse> saveproduct(@RequestBody ProductRequest productRequest) {
		ProductResponse createdProduct = productService.createProduct(productRequest);
		return new ResponseEntity<ProductResponse>(createdProduct, HttpStatus.CREATED);
	}
}
