package com.japes.productservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.japes.productservice.dto.category.CategoryResponse;
import com.japes.productservice.dto.category.CreateCategoryRequest;
import com.japes.productservice.dto.category.UpdateCategoryRequest;
import com.japes.productservice.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "APIs for managing product categories")
public class CategoryController {
	private final CategoryService categoryService;

	@PostMapping
    @Operation(summary = "Create Category", description = "Creates a new product category")
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody @Valid CreateCategoryRequest request) {

        log.info("Received request to create category '{}'", request.getName());

        CategoryResponse response = categoryService.saveCategory(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

	@GetMapping("/{id}")
	@Operation(summary = "Get Category By ID", description = "Fetch category details using category ID")
	public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id){
		log.info("Received request to fetch category with ID {}", id);
		CategoryResponse response = categoryService.getCategoryById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping
    @Operation(summary = "Get All Categories", description = "Fetch all available product categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.info("Received request to fetch all categories");
        List<CategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

	@PutMapping("/{id}")
	@Operation(summary = "Update Category", description = "Updates an existing category")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody @Valid UpdateCategoryRequest request) {
		log.info("Received request to update category {}", id);
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

	@DeleteMapping("/{id}")
    @Operation(summary = "Delete Category", description = "Deletes a category")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id) {

        log.info("Received request to delete category {}", id);

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}
