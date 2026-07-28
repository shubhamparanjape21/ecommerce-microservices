package com.japes.productservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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
import com.japes.productservice.exception.category.CategoryInUseException;
import com.japes.productservice.exception.category.CategoryNotFoundException;
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
	    // Verify
	    verify(categoryRepository).existsByName(createRequest.getName());
	    verify(categoryRepository, never()).save(any(Category.class));
	    verify(modelMapper, never()).map(any(), eq(Category.class));
	    verifyNoMoreInteractions(categoryRepository, modelMapper);
	}
	
	@Test
	void shouldReturnCategoryById() {
	    // Arrange
	    when(categoryRepository.findById(category.getId()))
	            .thenReturn(Optional.of(category));
	    // Act
	    CategoryResponse response = categoryServiceImpl.getCategoryById(category.getId());
	    // Assert
	    assertNotNull(response);
	    assertEquals(category.getId(), response.getId());
	    assertEquals(category.getName(), response.getName());
	    assertEquals(category.getDescription(), response.getDescription());
	    // Verify
	    verify(categoryRepository).findById(category.getId());
	    verifyNoMoreInteractions(categoryRepository);
	}
	
	@Test
	void shouldThrowCategoryNotFoundException() {
	    // Arrange
	    Long categoryId = 1L;
	    // Mock
	    when(categoryRepository.findById(categoryId))
	            .thenReturn(Optional.empty());

	    // Act & Assert
	    CategoryNotFoundException exception = assertThrows(
	            CategoryNotFoundException.class,
	            () -> categoryServiceImpl.getCategoryById(categoryId)
	    );
	    assertEquals(
	            "Category with ID " + categoryId + " not found",
	            exception.getMessage()
	    );
	    // Verify
	    verify(categoryRepository).findById(categoryId);
	    verifyNoMoreInteractions(categoryRepository);
	}
	
	@Test
	void shouldReturnAllCategories() {
	    // Arrange
	    Category category2 = new Category();
	    category2.setId(2L);
	    category2.setName("Fashion");
	    category2.setDescription("Clothing and Accessories");

	    List<Category> categories = List.of(category, category2);

	    when(categoryRepository.findAll()).thenReturn(categories);

	    // Act
	    List<CategoryResponse> responses = categoryServiceImpl.getAllCategories();

	    // Assert
	    assertNotNull(responses);
	    assertEquals(2, responses.size());

	    assertEquals(category.getId(), responses.get(0).getId());
	    assertEquals(category.getName(), responses.get(0).getName());
	    assertEquals(category.getDescription(), responses.get(0).getDescription());

	    assertEquals(category2.getId(), responses.get(1).getId());
	    assertEquals(category2.getName(), responses.get(1).getName());
	    assertEquals(category2.getDescription(), responses.get(1).getDescription());
	    // Verify
	    verify(categoryRepository).findAll();
	    verifyNoMoreInteractions(categoryRepository);
	}
	
	@Test
	void shouldUpdateCategorySuccessfully() {
	    // Arrange
	    when(categoryRepository.findById(category.getId()))
	            .thenReturn(Optional.of(category));

	    when(categoryRepository.existsByName(updateRequest.getName()))
	            .thenReturn(false);

	    when(categoryRepository.save(any(Category.class)))
	            .thenReturn(category);

	    // Act
	    CategoryResponse response =
	            categoryServiceImpl.updateCategory(category.getId(), updateRequest);

	    // Assert
	    assertNotNull(response);

	    assertEquals(category.getId(), response.getId());
	    assertEquals(updateRequest.getName(), response.getName());
	    assertEquals(updateRequest.getDescription(), response.getDescription());

	    ArgumentCaptor<Category> categoryCaptor =
	            ArgumentCaptor.forClass(Category.class);

	    verify(categoryRepository).save(categoryCaptor.capture());

	    Category capturedCategory = categoryCaptor.getValue();

	    assertEquals(updateRequest.getName(), capturedCategory.getName());
	    assertEquals(updateRequest.getDescription(), capturedCategory.getDescription());
	    
	    // Verify
	    verify(categoryRepository).findById(category.getId());
	    verify(categoryRepository).existsByName(updateRequest.getName());
	    verify(categoryRepository).save(any(Category.class));
	    verifyNoMoreInteractions(categoryRepository);
	}
	
	@Test
	void shouldThrowCategoryNotFoundExceptionWhenUpdating() {
	    // Arrange
	    Long categoryId = 1L;

	    when(categoryRepository.findById(categoryId))
	            .thenReturn(Optional.empty());

	    // Act & Assert
	    CategoryNotFoundException exception = assertThrows(
	            CategoryNotFoundException.class,
	            () -> categoryServiceImpl.updateCategory(categoryId, updateRequest)
	    );

	    assertEquals(
	            "Category with ID " + categoryId + " not found",
	            exception.getMessage()
	    );

	    // Verify
	    verify(categoryRepository).findById(categoryId);
	    verify(categoryRepository, never()).existsByName(anyString());
	    verify(categoryRepository, never()).save(any(Category.class));
	    verifyNoMoreInteractions(categoryRepository);
	}
	
	@Test
	void shouldThrowCategoryAlreadyExistsExceptionWhenUpdating() {
	    // Arrange
	    when(categoryRepository.findById(category.getId()))
	            .thenReturn(Optional.of(category));

	    when(categoryRepository.existsByName(updateRequest.getName()))
	            .thenReturn(true);

	    // Act & Assert
	    CategoryAlreadyExistsException exception = assertThrows(
	            CategoryAlreadyExistsException.class,
	            () -> categoryServiceImpl.updateCategory(category.getId(), updateRequest)
	    );

	    assertEquals(
	            "Category with name " + updateRequest.getName() + " already exists",
	            exception.getMessage()
	    );

	    // Verify
	    verify(categoryRepository).findById(category.getId());
	    verify(categoryRepository).existsByName(updateRequest.getName());
	    verify(categoryRepository, never()).save(any(Category.class));
	    verifyNoMoreInteractions(categoryRepository);
	}
	
	@Test
	void shouldDeleteCategorySuccessfully() {
	    // Arrange
	    when(categoryRepository.findById(category.getId()))
	            .thenReturn(Optional.of(category));

	    when(productRepository.existsByCategoryId(category.getId()))
	            .thenReturn(false);

	    // Act
	    categoryServiceImpl.deleteCategory(category.getId());

	    // Verify
	    verify(categoryRepository).findById(category.getId());
	    verify(productRepository).existsByCategoryId(category.getId());
	    verify(categoryRepository).delete(category);
	    verifyNoMoreInteractions(categoryRepository, productRepository);
	}
	
	@Test
	void shouldThrowCategoryInUseException() {
	    // Arrange
	    when(categoryRepository.findById(category.getId()))
	            .thenReturn(Optional.of(category));

	    when(productRepository.existsByCategoryId(category.getId()))
	            .thenReturn(true);

	    // Act & Assert
	    CategoryInUseException exception = assertThrows(
	            CategoryInUseException.class,
	            () -> categoryServiceImpl.deleteCategory(category.getId())
	    );

	    assertEquals(
	            "Category cannot be deleted because it is associated with existing products",
	            exception.getMessage()
	    );

	    // Verify
	    verify(categoryRepository).findById(category.getId());
	    verify(productRepository).existsByCategoryId(category.getId());
	    verify(categoryRepository, never()).delete(any(Category.class));
	    verifyNoMoreInteractions(categoryRepository, productRepository);
	}
}
