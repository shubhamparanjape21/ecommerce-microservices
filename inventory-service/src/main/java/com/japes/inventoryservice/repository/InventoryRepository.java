package com.japes.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.inventoryservice.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

}
