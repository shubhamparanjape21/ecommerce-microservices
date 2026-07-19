package com.japes.inventoryservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryResponse;
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
}
