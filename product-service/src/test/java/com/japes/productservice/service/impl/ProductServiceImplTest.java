package com.japes.productservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

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

import com.japes.productservice.dto.product.CreateProductRequest;
import com.japes.productservice.dto.product.ProductPageResponse;
import com.japes.productservice.dto.product.ProductResponse;
import com.japes.productservice.dto.product.UpdateProductRequest;
import com.japes.productservice.entity.Category;
import com.japes.productservice.entity.Product;
import com.japes.productservice.exception.category.CategoryNotFoundException;
import com.japes.productservice.exception.product.ProductAlreadyExistsException;
import com.japes.productservice.exception.product.ProductNotFoundException;
import com.japes.productservice.repository.CategoryRepository;
import com.japes.productservice.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private ProductServiceImpl productService;
	
	private CreateProductRequest createRequest;
	private Category category;
	private Product product;
	private ProductResponse response;
	private UpdateProductRequest updateRequest;

	@BeforeEach
	void setUp() {
		// Arrange
		// Create all required objects & fill those objects
		category = new Category();
	    category.setId(1L);
	    category.setName("Mobiles");
	    category.setDescription("Smartphones and Accessories");

	    createRequest = new CreateProductRequest();
	    createRequest.setName("iPhone 16");
	    createRequest.setDescription("Apple flagship phone");
	    createRequest.setBrand("Apple");
	    createRequest.setImageUrl("iphone16.jpg");
	    createRequest.setCategoryId(1L);

	    updateRequest = new UpdateProductRequest();
	    updateRequest.setName("iPhone 16 Pro");
	    updateRequest.setDescription("Updated Description");
	    updateRequest.setBrand("Apple");
	    updateRequest.setImageUrl("iphone16pro.jpg");
	    updateRequest.setCategoryId(1L);
	    updateRequest.setActive(true);

	    product = new Product();
	    product.setId(1L);
	    product.setName("iPhone 16");
	    product.setDescription("Apple flagship phone");
	    product.setBrand("Apple");
	    product.setImageUrl("iphone16.jpg");
	    product.setCategory(category);
	    product.setActive(true);

	    response = new ProductResponse();
	    response.setId(1L);
	    response.setName("iPhone 16");
	    response.setDescription("Apple flagship phone");
	    response.setBrand("Apple");
	    response.setImageUrl("iphone16.jpg");
	    response.setCategoryId(1L);
	    response.setCategoryName("Mobiles");
	    response.setActive(true);
	}

	@Test
	void saveProduct_ShouldCreateProductSuccessfully() {
	    // Arrange
	    when(categoryRepository.findById(createRequest.getCategoryId()))
	            .thenReturn(Optional.of(category));

	    when(productRepository.existsByNameAndBrand(
	            createRequest.getName(),
	            createRequest.getBrand()))
	            .thenReturn(false);

	    when(modelMapper.map(createRequest, Product.class))
	            .thenReturn(product);

	    when(productRepository.save(product))
	            .thenReturn(product);

	    // Act
	    ProductResponse result = productService.saveProduct(createRequest);

	    // Assert
	    assertNotNull(result);

	    assertEquals(product.getId(), result.getId());
	    assertEquals(product.getName(), result.getName());
	    assertEquals(product.getDescription(), result.getDescription());
	    assertEquals(product.getBrand(), result.getBrand());
	    assertEquals(product.getImageUrl(), result.getImageUrl());

	    assertEquals(category.getId(), result.getCategoryId());
	    assertEquals(category.getName(), result.getCategoryName());

	    // Verify
	    verify(categoryRepository).findById(createRequest.getCategoryId());

	    verify(productRepository)
	            .existsByNameAndBrand(
	                    createRequest.getName(),
	                    createRequest.getBrand());

	    verify(modelMapper).map(createRequest, Product.class);

	    verify(productRepository).save(product);
	}
	
	@Test
	void saveProduct_ShouldThrowCategoryNotFoundException() {
	    // Arrange
	    when(categoryRepository.findById(createRequest.getCategoryId()))
	            .thenReturn(Optional.empty());

	    // Act & Assert
	    CategoryNotFoundException exception =
	            assertThrows(CategoryNotFoundException.class,
	                    () -> productService.saveProduct(createRequest));

	    assertEquals(
	            "Category with ID 1 not found",
	            exception.getMessage());

	    // Verify
	    verify(categoryRepository)
	            .findById(createRequest.getCategoryId());

	    verify(productRepository, never())
	            .existsByNameAndBrand(anyString(), anyString());

	    verify(productRepository, never())
	            .save(any());

	    verify(modelMapper, never())
	            .map(any(), eq(Product.class));
	}
	
	@Test
	void saveProduct_ShouldThrowProductAlreadyExistsException() {
	    // Arrange
	    when(categoryRepository.findById(createRequest.getCategoryId()))
	            .thenReturn(Optional.of(category));

	    when(productRepository.existsByNameAndBrand(
	            createRequest.getName(),
	            createRequest.getBrand()))
	            .thenReturn(true);

	    // Act & Assert
	    ProductAlreadyExistsException exception =
	            assertThrows(ProductAlreadyExistsException.class,
	                    () -> productService.saveProduct(createRequest));

	    assertEquals(
	            "Product 'iPhone 16' of brand 'Apple' already exists",
	            exception.getMessage());

	    // Verify
	    verify(categoryRepository)
	            .findById(createRequest.getCategoryId());

	    verify(productRepository)
	            .existsByNameAndBrand(
	                    createRequest.getName(),
	                    createRequest.getBrand());

	    verify(productRepository, never())
	            .save(any());

	    verify(modelMapper, never())
	            .map(any(), eq(Product.class));
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
	void getProductById_ShouldReturnProductSuccessfully() {
	    // Arrange
	    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

	    // Act
	    ProductResponse result = productService.getProductById(product.getId());

	    // Assert
	    assertNotNull(result);

	    assertEquals(product.getId(), result.getId());
	    assertEquals(product.getName(), result.getName());
	    assertEquals(product.getDescription(), result.getDescription());
	    assertEquals(product.getBrand(), result.getBrand());
	    assertEquals(product.getImageUrl(), result.getImageUrl());

	    assertEquals(category.getId(), result.getCategoryId());
	    assertEquals(category.getName(), result.getCategoryName());
	    // Verify
	    verify(productRepository).findById(product.getId());
	}
	
	@Test
	void getProductById_ShouldThrowProductNotFoundException() {
	    // Arrange
	    when(productRepository.findById(1L)).thenReturn(Optional.empty());

	    // Act & Assert
	    ProductNotFoundException exception =
	            assertThrows(ProductNotFoundException.class,
	                    () -> productService.getProductById(1L));

	    assertEquals("Product with ID 1 not found", exception.getMessage());
	    // Verify
	    verify(productRepository).findById(1L);
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
	void testDeleteProduct_ProductNotFoundException() {
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
	
	@Test
	void shouldUpdateProductSuccessfully() {
		// mock repository
		when(productRepository.findById(savedProduct.getId())).thenReturn(Optional.of(savedProduct));
		// manually change the object state to represent its updated state
		savedProduct.setName(updateRequest.getName());
		savedProduct.setDescription(updateRequest.getDescription());
		savedProduct.setPrice(updateRequest.getPrice());
		// stub
		when(productRepository.save(savedProduct)).thenReturn(savedProduct);
		when(modelMapper.map(savedProduct, ProductResponse.class)).thenReturn(response);
		doNothing().when(modelMapper).map(updateRequest, savedProduct);
		// update response
		response.setName(updateRequest.getName());
		response.setDescription(updateRequest.getDescription());
		response.setPrice(updateRequest.getPrice());
		// Act
		ProductResponse result = productService.updateProduct(1L, updateRequest);
		// Assert
		assertNotNull(result);
		assertEquals(updateRequest.getSkuCode(), result.getSkuCode());
		assertEquals(updateRequest.getName(), result.getName());
		assertEquals(updateRequest.getDescription(), result.getDescription());
		assertEquals(updateRequest.getPrice(), result.getPrice());
		// verify
		verify(productRepository).findById(savedProduct.getId());
		verify(modelMapper).map(updateRequest, savedProduct);
		verify(productRepository).save(savedProduct);
		verify(modelMapper).map(savedProduct, ProductResponse.class);
	}
	
	@Test
	void testUpdateProduct_ProductNotFoundException() {
		when(productRepository.findById(1L))
        .thenReturn(Optional.empty());
		ProductNotFoundException exception =
		        assertThrows(
		                ProductNotFoundException.class,
		                () -> productService.updateProduct(1L, updateRequest)
		        );
		assertEquals(
		        "Product with ID 1 not found",
		        exception.getMessage()
		);
		verify(productRepository).findById(1L);
		verify(productRepository, never()).save(any(Product.class));
		verifyNoInteractions(modelMapper);
	}
	
	@Test
	void testUpdateProduct_ProductAlreadyExistsException() {
		// Repository finds existing product
		when(productRepository.findById(1L))
        .thenReturn(Optional.of(savedProduct));
		// make the update request use a different SKU
		updateRequest.setSkuCode("SKU999");
		// stub
		when(productRepository.existsBySkuCode(updateRequest.getSkuCode())).thenReturn(true);
		// Act + Assert
		ProductAlreadyExistsException exception =
		        assertThrows(
		                ProductAlreadyExistsException.class,
		                () -> productService.updateProduct(1L, updateRequest)
		        );
		assertEquals(
		        "Another product with SKU SKU999 already exists",
		        exception.getMessage()
		);
		// verify 
		verify(productRepository).findById(1L); // repo is searched
		verify(productRepository).existsBySkuCode("SKU999"); // duplicate is executed
		verify(productRepository, never()).save(any(Product.class)); //save should never happen
		verifyNoInteractions(modelMapper); // mapper should never be called because exception is thrown before it
	}
	
	@Test
	void shouldReturnProductWhenSkuCodeExists() {
		// Arrange
		when(productRepository.findBySkuCode(savedProduct.getSkuCode())).thenReturn(Optional.of(savedProduct));
		when(modelMapper.map(savedProduct, ProductResponse.class)).thenReturn(response);
		// Act
		ProductResponse result = productService.getProductBySkuCode(savedProduct.getSkuCode());
		// Assert
		assertNotNull(result);
		assertEquals(savedProduct.getSkuCode(), result.getSkuCode());
		assertEquals(savedProduct.getName(), result.getName());
		assertEquals(savedProduct.getDescription(), result.getDescription());
		assertEquals(savedProduct.getPrice(), result.getPrice());
		// Verify
		verify(productRepository).findBySkuCode(savedProduct.getSkuCode());
		verify(modelMapper).map(savedProduct, ProductResponse.class);
	}
	
	@Test
	void testGetProductBySkuCode_ProductNotFoundException() {
		// Arrange
		when(productRepository.findBySkuCode("SKU001"))
        .thenReturn(Optional.empty());
		// Act + Assert
		ProductNotFoundException exception =
		        assertThrows(
		                ProductNotFoundException.class,
		                () -> productService.getProductBySkuCode("SKU001")
		        );
		// Assert
		assertEquals(
		        "Product with SkuCode SKU001 not found",
		        exception.getMessage()
		);
		// verify
		verify(productRepository).findBySkuCode("SKU001");
		verifyNoInteractions(modelMapper);
	}

}
