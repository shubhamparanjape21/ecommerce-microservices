package com.japes.productservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.data.domain.PageRequest;
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
	void getProductList_ShouldReturnPagedProductsSuccessfully() {
	    // Arrange
	    Page<Product> productPage = new PageImpl<>(
	            List.of(product),
	            PageRequest.of(0, 10),
	            1);

	    when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

	    // Act
	    ProductPageResponse result = productService.getProductList(0, 10, "name", "asc");

	    // Assert
	    assertNotNull(result);

	    assertEquals(1, result.getProducts().size());

	    ProductResponse response = result.getProducts().get(0);

	    assertEquals(product.getId(), response.getId());
	    assertEquals(product.getName(), response.getName());
	    assertEquals(product.getDescription(), response.getDescription());
	    assertEquals(product.getBrand(), response.getBrand());
	    assertEquals(product.getImageUrl(), response.getImageUrl());

	    assertEquals(category.getId(), response.getCategoryId());
	    assertEquals(category.getName(), response.getCategoryName());

	    assertEquals(0, result.getCurrentPage());
	    assertEquals(10, result.getPageSize());
	    assertEquals(1, result.getTotalElements());
	    assertEquals(1, result.getTotalPages());

	    assertTrue(result.isFirst());
	    assertTrue(result.isLast());
	    // Verify
	    verify(productRepository).findAll(any(Pageable.class));
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
	void deleteProduct_ShouldDeleteSuccessfully() {
		// mocking behaviour
		when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
		// Act
		productService.deleteProduct(product.getId());
		// Assert - nothing to assert because no return value
		// verify interactions
		verify(productRepository).findById(product.getId());
		verify(productRepository).delete(product);
	}

	@Test
	void deleteProduct_ShouldThrowProductNotFoundException() {
		// mock the repo
		when(productRepository.findById(product.getId())).thenReturn(Optional.empty());
		// Act + Assert
		ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(product.getId()));
		assertEquals("Product with ID 1 not found", exception.getMessage());
		// verify repository searched
		verify(productRepository).findById(product.getId());
		// verify delete should never happen
		verify(productRepository, never()).deleteById(anyLong());
	}

	@Test
	void updateProduct_ShouldUpdateSuccessfully() {
	    // Arrange
	    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
	    when(categoryRepository.findById(updateRequest.getCategoryId())).thenReturn(Optional.of(category));
	    when(productRepository.existsByNameAndBrand(updateRequest.getName(),updateRequest.getBrand())).thenReturn(false);
	    when(productRepository.save(product)).thenReturn(product);

	    // Act
	    ProductResponse result = productService.updateProduct(product.getId(), updateRequest);

	    // Assert
	    assertNotNull(result);

	    assertEquals(updateRequest.getName(), result.getName());
	    assertEquals(updateRequest.getDescription(), result.getDescription());
	    assertEquals(updateRequest.getBrand(), result.getBrand());
	    assertEquals(updateRequest.getImageUrl(), result.getImageUrl());

	    assertEquals(category.getId(), result.getCategoryId());
	    assertEquals(category.getName(), result.getCategoryName());

	    // Verify
	    verify(productRepository).findById(product.getId());
	    verify(categoryRepository).findById(updateRequest.getCategoryId());
	    verify(productRepository).save(product);
	}

	@Test
	void updateProduct_ShouldThrowProductNotFoundException() {
	    // Arrange
	    when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

	    // Act & Assert
	    ProductNotFoundException exception =
	            assertThrows(ProductNotFoundException.class,
	                    () -> productService.updateProduct(product.getId(), updateRequest));

	    assertEquals("Product with ID 1 not found", exception.getMessage());

	    // Verify
	    verify(productRepository).findById(product.getId());
	    verify(categoryRepository, never()).findById(anyLong());
	    verify(productRepository, never()).save(any());
	}

	@Test
	void updateProduct_ShouldThrowCategoryNotFoundException() {
	    // Arrange
	    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
	    when(categoryRepository.findById(updateRequest.getCategoryId())).thenReturn(Optional.empty());

	    // Act & Assert
	    CategoryNotFoundException exception =
	            assertThrows(CategoryNotFoundException.class,
	                    () -> productService.updateProduct(product.getId(), updateRequest));

	    assertEquals("Category with ID 1 not found", exception.getMessage());

	    // Verify
	    verify(productRepository).findById(product.getId());
	    verify(categoryRepository).findById(updateRequest.getCategoryId());
	    verify(productRepository, never()).save(any());
	}

	@Test
	void updateProduct_ShouldThrowProductAlreadyExistsException() {
	    // Arrange
	    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
	    when(categoryRepository.findById(updateRequest.getCategoryId())).thenReturn(Optional.of(category));
	    when(productRepository.existsByNameAndBrand(updateRequest.getName(),updateRequest.getBrand())).thenReturn(true);

	    // Act & Assert
	    ProductAlreadyExistsException exception =
	            assertThrows(ProductAlreadyExistsException.class,
	                    () -> productService.updateProduct(product.getId(), updateRequest));

	    assertEquals(
	            "Product 'iPhone 16 Pro' of brand 'Apple' already exists",
	            exception.getMessage());

	    // Verify
	    verify(productRepository).findById(product.getId());
	    verify(categoryRepository).findById(updateRequest.getCategoryId());
	    verify(productRepository, never()).save(any());
	}

}
