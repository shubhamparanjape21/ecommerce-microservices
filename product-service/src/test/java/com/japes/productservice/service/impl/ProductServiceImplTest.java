package com.japes.productservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.entity.Product;
import com.japes.productservice.exception.ProductAlreadyExistsException;
import com.japes.productservice.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
	@Mock
	private ProductRepository productRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private ProductServiceImpl productService;
	
	private CreateProductRequest request;
	private Product product;
	private Product savedProduct;
	private ProductResponse response;

	@BeforeEach
	void setUp() {
		// Arrange
		// Create all required objects & fill those objects
		request = new CreateProductRequest();
		request.setSkuCode("SKU001");
		request.setName("iPhone 16");
		request.setDescription("Latest Apple smartphone");
		request.setPrice(new BigDecimal("89999"));

		product = new Product();
		product.setSkuCode("SKU001");
		product.setName("iPhone 16");
		product.setDescription("Latest Apple smartphone");
		product.setPrice(new BigDecimal("89999"));

		savedProduct = new Product();
		savedProduct.setId(1L);
		savedProduct.setSkuCode("SKU001");
		savedProduct.setName("iPhone 16");
		savedProduct.setDescription("Latest Apple smartphone");
		savedProduct.setPrice(new BigDecimal("89999"));

		response = new ProductResponse();
		response.setId(1L);
		response.setSkuCode("SKU001");
		response.setName("iPhone 16");
		response.setDescription("Latest Apple smartphone");
		response.setPrice(new BigDecimal("89999"));
	}

	@Test
	void shouldSaveProductSuccessfully() {
		// Mock behaviour
		when(productRepository.existsBySkuCode(request.getSkuCode())).thenReturn(false);
		when(modelMapper.map(request, Product.class)).thenReturn(product);
		when(productRepository.save(product)).thenReturn(savedProduct);
		when(modelMapper.map(savedProduct, ProductResponse.class)).thenReturn(response);

		// Act
		// Call method
		ProductResponse result = productService.saveProduct(request);

		// Assert
		assertNotNull(result);
		assertEquals("SKU001", result.getSkuCode());
		assertEquals(1L, result.getId());
		assertEquals("iPhone 16", result.getName());
		assertEquals(new BigDecimal("89999"), result.getPrice());

		// verify
		verify(productRepository).existsBySkuCode(request.getSkuCode());
		verify(productRepository).save(product);
		verify(modelMapper).map(request, Product.class);
		verify(modelMapper).map(savedProduct, ProductResponse.class);
	}
	
	@Test
	void testSaveProduct_Exception() {
		// mock the repo
		when(productRepository.existsBySkuCode(request.getSkuCode())).thenReturn(true);
		// Act + Assert
		ProductAlreadyExistsException exception = assertThrows(ProductAlreadyExistsException.class, () -> productService.saveProduct(request));
		verify(productRepository, never()).save(any(Product.class));
		verify(modelMapper, never()).map(any(), eq(Product.class));
		verify(modelMapper, never()).map(any(), eq(ProductResponse.class));
		assertEquals("Product with SKU SKU001 already exists", exception.getMessage());
	}

}
