package com.japes.productservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.japes.productservice.dto.category.CategoryResponse;
import com.japes.productservice.dto.category.CreateCategoryRequest;
import com.japes.productservice.dto.category.UpdateCategoryRequest;
import com.japes.productservice.entity.Category;
import com.japes.productservice.exception.category.CategoryAlreadyExistsException;
import com.japes.productservice.repository.CategoryRepository;
import com.japes.productservice.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private CategoryServiceImpl categoryServiceImpl;
	
	private Category category;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;
    private CategoryResponse categoryResponse;
    
	@BeforeEach
	void setUp() {
		// Arrange
		createRequest = new CreateCategoryRequest();
	    createRequest.setName("Electronics");
	    createRequest.setDescription("Electronic gadgets");

	    updateRequest = new UpdateCategoryRequest();
	    updateRequest.setName("Mobiles");
	    updateRequest.setDescription("Mobile devices");

	    category = new Category();
	    category.setId(1L);
	    category.setName("Electronics");
	    category.setDescription("Electronic gadgets");

	    categoryResponse = new CategoryResponse();
	    categoryResponse.setId(1L);
	    categoryResponse.setName("Electronics");
	    categoryResponse.setDescription("Electronic gadgets");
	}
	
	@Test
	void shouldSaveCategorySuccessfully() {
		// Mock
		when(categoryRepository.existsByName(createRequest.getName())).thenReturn(false);
		when(modelMapper.map(createRequest, Category.class)).thenReturn(category);
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		// Act
		CategoryResponse response = categoryServiceImpl.saveCategory(createRequest);
		// Assert
		assertNotNull(response);
		assertEquals(1L, response.getId());
		assertEquals("Electronics", response.getName());
		assertEquals("Electronic gadgets", response.getDescription());
		
		ArgumentCaptor<Category> categoryCaptor =
	            ArgumentCaptor.forClass(Category.class);

	    verify(categoryRepository).save(categoryCaptor.capture());

	    Category capturedCategory = categoryCaptor.getValue();

	    assertEquals(createRequest.getName(), capturedCategory.getName());
	    assertEquals(createRequest.getDescription(), capturedCategory.getDescription());
		// Verify
		verify(categoryRepository).existsByName(createRequest.getName());
		verify(modelMapper).map(createRequest, Category.class);
		verify(categoryRepository).save(any(Category.class));
		verifyNoMoreInteractions(categoryRepository, modelMapper);
	}
	
	@Test
	void shouldThrowCategoryAlreadyExistsException() {
	    // Arrange
	    when(categoryRepository.existsByName(createRequest.getName()))
	            .thenReturn(true);
	    // Act & Assert
	    CategoryAlreadyExistsException exception = assertThrows(
	            CategoryAlreadyExistsException.class,
	            () -> categoryServiceImpl.saveCategory(createRequest)
	    );
	    assertEquals(
	            "Category with name '" + createRequest.getName() + "' already exists",
	            exception.getMessage()
	    );
	    verify(categoryRepository).existsByName(createRequest.getName());
	    verify(categoryRepository, never()).save(any(Category.class));
	    verify(modelMapper, never()).map(any(), eq(Category.class));
	    verifyNoMoreInteractions(categoryRepository, modelMapper);
	}
}
