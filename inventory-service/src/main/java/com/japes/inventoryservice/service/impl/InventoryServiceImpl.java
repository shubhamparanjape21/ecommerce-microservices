package com.japes.inventoryservice.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryResponse;
import com.japes.inventoryservice.entity.Inventory;
import com.japes.inventoryservice.repository.InventoryRepository;
import com.japes.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
	private final InventoryRepository inventoryRepository;
	private final ModelMapper modelMapper;

	@Override
	public InventoryResponse saveInventory(CreateInventoryRequest request) {
		Inventory inventory = modelMapper.map(request, Inventory.class);
		Inventory savedInventory = inventoryRepository.save(inventory);
		return modelMapper.map(savedInventory, InventoryResponse.class);
	}
}
