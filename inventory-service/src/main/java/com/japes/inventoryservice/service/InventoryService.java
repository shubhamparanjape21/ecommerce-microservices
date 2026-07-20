package com.japes.inventoryservice.service;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryPageResponse;
import com.japes.inventoryservice.dto.InventoryResponse;

public interface InventoryService {
	InventoryResponse saveInventory(CreateInventoryRequest request);
	InventoryPageResponse getInventoryList(int page, int size, String sortBy, String direction);
	InventoryResponse getInventoryById(Long id);
	InventoryResponse getInventoryBySkuCode(String skuCode);
}
