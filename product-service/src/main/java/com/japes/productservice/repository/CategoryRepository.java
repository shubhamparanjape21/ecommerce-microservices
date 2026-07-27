package com.japes.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.productservice.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByName(String name);
}
