package com.japes.productservice.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.japes.productservice.dto.category.CategoryResponse;
import com.japes.productservice.dto.category.CreateCategoryRequest;
import com.japes.productservice.entity.Category;
import com.japes.productservice.exception.category.CategoryAlreadyExistsException;
import com.japes.productservice.exception.category.CategoryNotFoundException;
import com.japes.productservice.repository.CategoryRepository;
import com.japes.productservice.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;

	@Override
	public CategoryResponse saveCategory(CreateCategoryRequest request) {
		log.info("Creating category with name '{}'", request.getName());

        log.debug("Checking whether category '{}' already exists", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            log.warn("Duplicate category creation attempted with name '{}'", request.getName());
            throw new CategoryAlreadyExistsException(
                    "Category with name '" + request.getName() + "' already exists");
        }

        log.debug("Mapping CreateCategoryRequest to Category entity");

        Category category = modelMapper.map(request, Category.class);

        log.debug("Saving category to database");

        Category savedCategory = categoryRepository.save(category);

        log.info("Successfully created category with ID {}", savedCategory.getId());

        return mapToCategoryResponse(savedCategory);
	}
	
	public CategoryResponse mapToCategoryResponse(Category category) {
		CategoryResponse response = new CategoryResponse();
		
		response.setId(category.getId());
		response.setName(category.getName());
		response.setDescription(category.getDescription());
		
		return response;
	}

	@Override
	public CategoryResponse getCategoryById(Long id) {
		log.info("Fetching category details for ID {}", id);
		log.debug("Checking whether category with ID {} exists", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID {}", id);
                    return new CategoryNotFoundException(
                            "Category with ID " + id + " not found");
                });

        log.info("Successfully fetched category with ID {}", id);

        return mapToCategoryResponse(category);
	}

}
