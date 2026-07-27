package com.japes.productservice.service;

import java.util.List;

import com.japes.productservice.dto.category.CategoryResponse;
import com.japes.productservice.dto.category.CreateCategoryRequest;

public interface CategoryService {
	CategoryResponse saveCategory(CreateCategoryRequest request);
	CategoryResponse getCategoryById(Long id);
	List<CategoryResponse> getAllCategories();
}
