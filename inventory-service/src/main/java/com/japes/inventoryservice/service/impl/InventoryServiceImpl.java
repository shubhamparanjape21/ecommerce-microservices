package com.japes.inventoryservice.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryResponse;
import com.japes.inventoryservice.entity.Inventory;
import com.japes.inventoryservice.exception.InventoryAlreadyExistsException;
import com.japes.inventoryservice.repository.InventoryRepository;
import com.japes.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
	private final InventoryRepository inventoryRepository;
	private final ModelMapper modelMapper;

	@Override
	public InventoryResponse saveInventory(CreateInventoryRequest request) {
		log.info("Creating inventory with SKU {}", request.getSkuCode());
		log.debug("Checking if inventory with SKU {} already exists", request.getSkuCode());
		if(inventoryRepository.existsBySkuCode(request.getSkuCode())) {
			log.warn("Duplicate inventory creation attempted for SKU {}", request.getSkuCode());
			throw new InventoryAlreadyExistsException("Inventory with SKU " + request.getSkuCode() + " already exists");
		}
		log.debug("Mapping CreateInventoryRequest to Inventory entity");
		Inventory inventory = modelMapper.map(request, Inventory.class);
		log.debug("Saving inventory to database");
		Inventory savedInventory = inventoryRepository.save(inventory);
		log.info("Successfully created inventory with ID {}", savedInventory.getId());
		return modelMapper.map(savedInventory, InventoryResponse.class);
	}
}
