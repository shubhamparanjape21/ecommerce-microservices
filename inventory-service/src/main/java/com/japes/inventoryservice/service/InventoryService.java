package com.japes.inventoryservice.service;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryResponse;

public interface InventoryService {
	InventoryResponse saveInventory(CreateInventoryRequest request);
}
