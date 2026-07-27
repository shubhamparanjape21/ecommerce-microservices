package com.japes.productservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.productservice.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Page<Product> findByActiveTrue(Pageable pageable);
	Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
	boolean existsByCategoryId(Long categoryId);
}
