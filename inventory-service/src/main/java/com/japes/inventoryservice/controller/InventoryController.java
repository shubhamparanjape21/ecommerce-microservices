package com.japes.inventoryservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryPageResponse;
import com.japes.inventoryservice.dto.InventoryResponse;
import com.japes.inventoryservice.dto.UpdateInventoryRequest;
import com.japes.inventoryservice.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {
	private final InventoryService inventoryService;
	
	@PostMapping
	public ResponseEntity<InventoryResponse> saveInventory(@RequestBody @Valid CreateInventoryRequest request) {
		log.info("Received request to create inventory with SKU {}", request.getSkuCode());
		InventoryResponse createdInventory = inventoryService.saveInventory(request);
		return new ResponseEntity<InventoryResponse>(createdInventory, HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<InventoryPageResponse> getInventoryList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction) {
		log.info("Received request to fetch inventories - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);
		InventoryPageResponse response = inventoryService.getInventoryList(page, size, sortBy, direction);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
		InventoryResponse response = inventoryService.getInventoryById(id);
		return new ResponseEntity<InventoryResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping("/sku/{skuCode}")
	public ResponseEntity<InventoryResponse> getInventoryBySkuCode(@PathVariable String skuCode) {
		InventoryResponse response = inventoryService.getInventoryBySkuCode(skuCode);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Long id, @RequestBody @Valid UpdateInventoryRequest updateRequest) {
		InventoryResponse response = inventoryService.updateInventory(id, updateRequest);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<InventoryResponse> deleteInventoryById(@PathVariable Long id) {
		inventoryService.deleteInventory(id);
		return ResponseEntity.noContent().build();
	}
}
