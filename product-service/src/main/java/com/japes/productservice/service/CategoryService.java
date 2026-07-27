package com.japes.productservice.service;

import java.util.List;

import com.japes.productservice.dto.category.CategoryResponse;
import com.japes.productservice.dto.category.CreateCategoryRequest;
import com.japes.productservice.dto.category.UpdateCategoryRequest;

public interface CategoryService {
	CategoryResponse saveCategory(CreateCategoryRequest request);
	CategoryResponse getCategoryById(Long id);
	List<CategoryResponse> getAllCategories();
	CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);
}
