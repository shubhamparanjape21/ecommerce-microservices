package com.japes.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.productservice.entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
	
}
