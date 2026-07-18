package com.japes.productservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.japes.productservice.dto.CreateProductRequest;
import com.japes.productservice.dto.ProductPageResponse;
import com.japes.productservice.dto.ProductResponse;
import com.japes.productservice.entity.Product;
import com.japes.productservice.exception.ProductAlreadyExistsException;
import com.japes.productservice.exception.ProductNotFoundException;
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
		product.setSkuCode(request.getSkuCode());
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());

		savedProduct = new Product();
		savedProduct.setId(1L);
		savedProduct.setSkuCode(request.getSkuCode());
		savedProduct.setName(request.getName());
		savedProduct.setDescription(request.getDescription());
		savedProduct.setPrice(request.getPrice());

		response = new ProductResponse();
		response.setId(1L);
		response.setSkuCode(request.getSkuCode());
		response.setName(request.getName());
		response.setDescription(request.getDescription());
		response.setPrice(request.getPrice());
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
	
	@Test
	void shouldReturnPaginatedProductList() {

		Product product2 = new Product();
		product2.setId(2L);
		product2.setSkuCode("SKU002");
		product2.setName("Samsung S25");
		product2.setDescription("Samsung flagship smartphone");
		product2.setPrice(new BigDecimal("75000"));
		
		ProductResponse response2 = new ProductResponse();
		response2.setId(product2.getId());
		response2.setSkuCode(product2.getSkuCode());
		response2.setName(product2.getName());
		response2.setDescription(product2.getDescription());
		response2.setPrice(product2.getPrice());
		
		List<Product> products = List.of(savedProduct, product2);
		Page<Product> productPage = new PageImpl<>(products); // it simulates to productRepository.findAll(pageable);
		
		when(productRepository.findAll(any(Pageable.class)))
        .thenReturn(productPage);
		when(modelMapper.map(savedProduct, ProductResponse.class))
		.thenReturn(response);
		when(modelMapper.map(product2, ProductResponse.class))
		.thenReturn(response2);
		// Act
		ProductPageResponse result = productService.getProductList(0, 5, "name", "asc");
		//Assert
		assertNotNull(result);
		assertEquals(2, result.getProducts().size());
		assertEquals(0, result.getCurrentPage());
		assertEquals(2, result.getTotalElements());
		assertTrue(result.isFirst());
		assertTrue(result.isLast());
		// verify
		verify(productRepository).findAll(any(Pageable.class));
		verify(modelMapper).map(savedProduct, ProductResponse.class);
		verify(modelMapper).map(product2, ProductResponse.class);
	}
	
	@Test
	void shouldReturnProductWhenIdExists() {
		// mocking behaviour
		when(productRepository.findById(savedProduct.getId())).thenReturn(Optional.of(savedProduct));
		when(modelMapper.map(savedProduct, ProductResponse.class)).thenReturn(response);
		// Act
		ProductResponse result = productService.getProductById(1L);
		// Assert
		assertNotNull(result);
		assertEquals("SKU001", result.getSkuCode());
		assertEquals("iPhone 16", result.getName());
		// verify
		verify(productRepository).findById(1L);
		verify(modelMapper).map(savedProduct, ProductResponse.class);
	}
	
	@Test
	void shouldThrowExceptionWhenProductNotFound() {
		// mock the repo
		when(productRepository.findById(1L)).thenReturn(Optional.empty());
		// Act + Assert
		ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
		assertEquals("Product with ID 1 not found", exception.getMessage());
		verify(productRepository).findById(1L);
		verifyNoInteractions(modelMapper);
	}
	
	@Test
	void shouldDeleteProductSuccessfully() {
		// mocking behaviour
		when(productRepository.findById(savedProduct.getId())).thenReturn(Optional.of(savedProduct));
		// Act
		productService.deleteProduct(savedProduct.getId());
		// Assert - nothing to assert because no return value
		// verify interactions
		verify(productRepository).findById(savedProduct.getId());
		verify(productRepository).deleteById(savedProduct.getId());
	}
	
	@Test
	void testDeleteProduct_Exception() {
		// mock the repo
		when(productRepository.findById(savedProduct.getId())).thenReturn(Optional.empty());
		// Act + Assert
		ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(savedProduct.getId()));
		assertEquals("Product with ID 1 not found", exception.getMessage());
		// verify repository searched
		verify(productRepository).findById(savedProduct.getId());
		// verify delete should never happen
		verify(productRepository, never()).deleteById(anyLong());
	}

}
