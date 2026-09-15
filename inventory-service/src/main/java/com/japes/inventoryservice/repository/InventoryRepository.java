package com.japes.inventoryservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.japes.inventoryservice.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	boolean existsBySkuCode(String skuCode);

	Optional<Inventory> findBySkuCode(String skuCode);

	// Atomic, single round-trip update guarded by the WHERE clause -
	// avoids the read-then-write race window that a find + check + save has.
	// Returns 0 rows updated when the SKU doesn't exist OR when quantity is
	// insufficient, so the caller distinguishes those cases separately.
	@Modifying
	@Query("UPDATE Inventory i SET i.quantity = i.quantity - :quantity "
			+ "WHERE i.skuCode = :skuCode AND i.quantity >= :quantity")
	int reduceStock(@Param("skuCode") String skuCode, @Param("quantity") int quantity);

	@Modifying
	@Query("UPDATE Inventory i SET i.quantity = i.quantity + :quantity " + "WHERE i.skuCode = :skuCode")
	int increaseStock(@Param("skuCode") String skuCode, @Param("quantity") int quantity);
}
