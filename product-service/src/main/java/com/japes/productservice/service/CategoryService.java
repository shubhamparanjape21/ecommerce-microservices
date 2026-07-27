package com.japes.productservice.service;

import com.japes.productservice.dto.category.CategoryResponse;
import com.japes.productservice.dto.category.CreateCategoryRequest;

public interface CategoryService {
	CategoryResponse saveCategory(CreateCategoryRequest request);
}
