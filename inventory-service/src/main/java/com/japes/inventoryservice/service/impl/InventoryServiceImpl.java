package com.japes.inventoryservice.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryPageResponse;
import com.japes.inventoryservice.dto.InventoryResponse;
import com.japes.inventoryservice.dto.UpdateInventoryRequest;
import com.japes.inventoryservice.entity.Inventory;
import com.japes.inventoryservice.exception.InsufficientInventoryException;
import com.japes.inventoryservice.exception.InventoryAlreadyExistsException;
import com.japes.inventoryservice.exception.InventoryNotFoundException;
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
		Inventory inventory = new Inventory();
		inventory.setSkuCode(request.getSkuCode());
		inventory.setQuantity(request.getQuantity());
		log.debug("Saving inventory to database");
		Inventory savedInventory = inventoryRepository.save(inventory);
		log.info("Successfully created inventory with ID {}", savedInventory.getId());
		return modelMapper.map(savedInventory, InventoryResponse.class);
	}

	@Override
	public InventoryPageResponse getInventoryList(int page, int size, String sortBy, String direction) {
		log.info("Fetching inventories - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);
		Sort sort = direction.equalsIgnoreCase("asc")
					? Sort.by(sortBy).ascending()
					: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);
		log.debug("Retrieved {} inventory records from database", inventoryPage.getNumberOfElements());
		log.debug("Mapping Inventory entities to InventoryResponse DTOs");
		List<InventoryResponse> inventories = inventoryPage.getContent()
					.stream()
					.map(inventory -> modelMapper.map(inventory, InventoryResponse.class))
					.toList();
		InventoryPageResponse response = new InventoryPageResponse(inventories, inventoryPage.getNumber(), inventoryPage.getTotalPages(), inventoryPage.getTotalElements(), inventoryPage.getSize(), inventoryPage.isFirst(), inventoryPage.isLast());
		log.info(
			    "Successfully fetched {} inventory records (page {} of {})",
			    inventoryPage.getNumberOfElements(),
			    inventoryPage.getNumber(),
			    inventoryPage.getTotalPages()
			);
		return response;
	}

	@Override
	public InventoryResponse getInventoryById(Long id) {
		log.info("Received request to fetch inventory with ID {}", id);
		log.debug("Checking whether inventory with ID {} exists", id);
		Inventory inventory = inventoryRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Inventory not found with ID {}", id);
					return new InventoryNotFoundException("Inventory with ID " + id + " not found");
				});
		log.debug("Mapping Inventory entity to InventoryResponse");
		InventoryResponse response = modelMapper.map(inventory, InventoryResponse.class);
		log.info("Successfully fetched inventory with ID {}", id);
		return response;
	}

	@Override
	public InventoryResponse getInventoryBySkuCode(String skuCode) {
		log.info("Received request to fetch inventory with SKU {}", skuCode);
		log.debug("Checking whether inventory with SKU {} exists", skuCode);
		Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
				.orElseThrow(() -> {
					log.warn("Inventory not found with SKU {}", skuCode);
					return new InventoryNotFoundException("Inventory with SKU " + skuCode + " not found");
				});
		log.debug("Mapping Inventory entity to InventoryResponse");
		InventoryResponse response = modelMapper.map(inventory, InventoryResponse.class);
		log.info("Successfully fetched inventory with SKU {}", skuCode);
		return response;
	}

	@Override
	public InventoryResponse updateInventory(Long id, UpdateInventoryRequest updateRequest) {
		log.info("Received request to update inventory with ID {}", id);
		log.debug("Checking whether inventory with ID {} exists", id);
		Inventory existingInventory = inventoryRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Inventory not found with ID {}", id);
					return new InventoryNotFoundException("Inventory with ID " + id + " not found");
				});
		log.debug("Mapping UpdateInventoryRequest to existing inventory entity");
		existingInventory.setQuantity(updateRequest.getQuantity());
		log.debug("Saving updated inventory to database");
		Inventory savedInventory = inventoryRepository.save(existingInventory);
		log.debug("Mapping Inventory entity to InventoryResponse");
		InventoryResponse response = modelMapper.map(savedInventory, InventoryResponse.class);
		log.info("Successfully updated inventory with ID {}", response.getId());
		return response;
	}

	@Override
	public void deleteInventory(Long id) {
		log.info("Received request to delete inventory with ID {}", id);
		log.debug("Checking whether inventory with ID {} exists", id);
		Inventory existingInventory = inventoryRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Inventory not found with ID {}", id);
					return new InventoryNotFoundException("Inventory with ID " + id + " not found");
				});
		log.debug("Deleting inventory from database");
		inventoryRepository.delete(existingInventory);
		log.info("Successfully deleted inventory with ID {}", id);
	}

	@Override
	public void reduceInventory(String skuCode, int quantity) {
		log.info("Reducing inventory for SKU {} by {}", skuCode, quantity);

	    Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
	            .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for SKU " + skuCode));

	    log.debug("Current inventory for SKU {} is {}", skuCode, inventory.getQuantity());

	    if (inventory.getQuantity() < quantity) {
	        log.warn("Insufficient inventory for SKU {}. Available: {}, Requested: {}", skuCode, inventory.getQuantity(), quantity);

	        throw new InsufficientInventoryException("Insufficient inventory for SKU " + skuCode);
	    }
	    inventory.setQuantity(inventory.getQuantity() - quantity);
	    inventoryRepository.save(inventory);
	    log.info("Inventory successfully reduced for SKU {}. Remaining quantity: {}", skuCode, inventory.getQuantity());
	}
}
