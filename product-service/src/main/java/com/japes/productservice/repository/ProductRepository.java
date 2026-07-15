package com.japes.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.productservice.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	boolean existsBySkuCode(String skuCode);
}
