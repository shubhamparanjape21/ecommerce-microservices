package com.japes.inventoryservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.inventoryservice.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	boolean existsBySkuCode(String skuCode);
	Optional<Inventory> findBySkuCode(String skuCode);
}
