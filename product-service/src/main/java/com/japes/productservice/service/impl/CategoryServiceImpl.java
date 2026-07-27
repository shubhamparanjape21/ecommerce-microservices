package com.japes.productservice.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.japes.productservice.dto.category.CategoryResponse;
import com.japes.productservice.dto.category.CreateCategoryRequest;
import com.japes.productservice.dto.category.UpdateCategoryRequest;
import com.japes.productservice.entity.Category;
import com.japes.productservice.exception.category.CategoryAlreadyExistsException;
import com.japes.productservice.exception.category.CategoryInUseException;
import com.japes.productservice.exception.category.CategoryNotFoundException;
import com.japes.productservice.repository.CategoryRepository;
import com.japes.productservice.repository.ProductRepository;
import com.japes.productservice.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ProductRepository productRepository;
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

	@Override
	public List<CategoryResponse> getAllCategories() {
		log.info("Fetching all categories");
		List<Category> categories = categoryRepository.findAll();
		log.debug("Retrieved {} categories from database", categories.size());
		List<CategoryResponse> response = categories.stream()
				.map(this::mapToCategoryResponse)
				.toList();
		log.info("Successfully fetched {} categories", response.size());
        return response;
	}

	@Override
	public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
		log.info("Updating category with ID {}", id);
		
        log.debug("Checking whether category with ID {} exists", id);
        
        Category category = categoryRepository.findById(id)
        		.orElseThrow(() -> {
        			log.warn("Category not found with ID {}", id);
        			return new CategoryNotFoundException("Category with ID " + id + " not found");
        		});
        
        if(!category.getName().equalsIgnoreCase(request.getName()) && categoryRepository.existsByName(request.getName())) {
        	log.warn("Another category with name {} already exists", request.getName());
        	throw new CategoryAlreadyExistsException("Category with name " + request.getName() + " already exists");
        }
        log.debug("Updating category fields");
        
        category.setName(request.getName());
        
        category.setDescription(request.getDescription());
        
        log.debug("Saving updated category");
        
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Successfully updated category with ID {}", id);
        
        return mapToCategoryResponse(updatedCategory);
	}

	@Override
	public void deleteCategory(Long id) {
		log.info("Received request to delete category with ID {}", id);

        log.debug("Checking whether category with ID {} exists", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID {}", id);
                    return new CategoryNotFoundException(
                            "Category with ID " + id + " not found");
                });
        if(productRepository.existsByCategoryId(id)) {
        	log.warn("Category is associated with existing products");
        	throw new CategoryInUseException("Category cannot be deleted because it is associated with existing products");
        }
        log.debug("Deleting category from database");

        categoryRepository.delete(category);

        log.info("Successfully deleted category with ID {}", id);
	}

}
