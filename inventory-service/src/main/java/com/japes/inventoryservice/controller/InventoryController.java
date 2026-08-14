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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(
	    name = "Inventory Management",
	    description = "REST APIs for managing product inventory"
	)
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {
	private final InventoryService inventoryService;
	
	@Operation(
		    summary = "Create Inventory",
		    description = "Creates a new inventory record for a product."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "201", description = "Inventory created successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid inventory data"),
		    @ApiResponse(responseCode = "409", description = "Inventory with the given SKU already exists")
		})
	@PostMapping
	public ResponseEntity<InventoryResponse> saveInventory(@RequestBody @Valid CreateInventoryRequest request) {
		log.info("Received request to create inventory with SKU {}", request.getSkuCode());
		InventoryResponse createdInventory = inventoryService.saveInventory(request);
		return new ResponseEntity<InventoryResponse>(createdInventory, HttpStatus.CREATED);
	}
	
	@Operation(
		    summary = "Get Inventory List",
		    description = "Retrieves paginated inventory records with sorting support."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Inventory list retrieved successfully")
		})
	@GetMapping
	public ResponseEntity<InventoryPageResponse> getInventoryList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction) {
		log.info("Received request to fetch inventories - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);
		InventoryPageResponse response = inventoryService.getInventoryList(page, size, sortBy, direction);
		return ResponseEntity.ok(response);
	}
	
	@Operation(
		    summary = "Get Inventory by ID",
		    description = "Retrieves inventory details for the specified inventory ID."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully"),
		    @ApiResponse(responseCode = "404", description = "Inventory not found")
		})
	@GetMapping("/{id}")
	public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
		InventoryResponse response = inventoryService.getInventoryById(id);
		return new ResponseEntity<InventoryResponse>(response, HttpStatus.OK);
	}
	
	@Operation(
		    summary = "Get Inventory by SKU",
		    description = "Retrieves inventory details for the specified SKU code."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully"),
		    @ApiResponse(responseCode = "404", description = "Inventory not found")
		})
	@GetMapping("/sku/{skuCode}")
	public ResponseEntity<InventoryResponse> getInventoryBySkuCode(@PathVariable String skuCode) {
		InventoryResponse response = inventoryService.getInventoryBySkuCode(skuCode);
		return ResponseEntity.ok(response);
	}
	
	@Operation(
		    summary = "Update Inventory",
		    description = "Updates an existing inventory record by ID."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid inventory data"),
		    @ApiResponse(responseCode = "404", description = "Inventory not found"),
		    @ApiResponse(responseCode = "409", description = "Inventory with the given SKU already exists")
		})
	@PutMapping("/update/{id}")
	public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Long id, @RequestBody @Valid UpdateInventoryRequest updateRequest) {
		InventoryResponse response = inventoryService.updateInventory(id, updateRequest);
		return ResponseEntity.ok(response);
	}
	
	@Operation(
		    summary = "Delete Inventory",
		    description = "Deletes an inventory record by its ID."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "204", description = "Inventory deleted successfully"),
		    @ApiResponse(responseCode = "404", description = "Inventory not found")
		})
	@DeleteMapping("/{id}")
	public ResponseEntity<InventoryResponse> deleteInventoryById(@PathVariable Long id) {
		inventoryService.deleteInventory(id);
		return ResponseEntity.noContent().build();
	}
	
	
	@Operation(
	        summary = "Reduce inventory",
	        description = "Reduces the available inventory quantity for a product variant identified by its SKU code."
	)
	@ApiResponses({
	        @ApiResponse(
	                responseCode = "204",
	                description = "Inventory reduced successfully"
	        ),
	        @ApiResponse(
	                responseCode = "400",
	                description = "Invalid SKU code or quantity"
	        ),
	        @ApiResponse(
	                responseCode = "404",
	                description = "Inventory not found for the given SKU code"
	        ),
	        @ApiResponse(
	                responseCode = "409",
	                description = "Insufficient inventory"
	        )
	})
	@PutMapping("/reduce")
	public ResponseEntity<Void> reduceInventory(
	        @Parameter(description = "SKU code of the product variant", example = "SHOE-NIKE-AIR-M-BLK")
	        @RequestParam String skuCode,

	        @Parameter(description = "Quantity to reduce from inventory", example = "2")
	        @RequestParam int quantity) {

	    inventoryService.reduceInventory(skuCode, quantity);
	    return ResponseEntity.noContent().build();
	}
}
