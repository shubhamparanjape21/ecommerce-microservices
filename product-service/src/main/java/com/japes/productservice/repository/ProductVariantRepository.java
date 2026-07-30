package com.japes.productservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.productservice.entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
	boolean existsBySkuCode(String skuCode);
	Optional<ProductVariant> findBySkuCode(String skuCode);
	List<ProductVariant> findByProductId(Long productId);
}
