package com.japes.inventoryservice.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryResponse;
import com.japes.inventoryservice.dto.UpdateInventoryRequest;
import com.japes.inventoryservice.entity.Inventory;
import com.japes.inventoryservice.exception.InventoryAlreadyExistsException;
import com.japes.inventoryservice.exception.InventoryNotFoundException;
import com.japes.inventoryservice.repository.InventoryRepository;
import com.japes.inventoryservice.service.impl.InventoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {
	@Mock
	private InventoryRepository inventoryRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private InventoryServiceImpl inventoryServiceImpl;
	
	private CreateInventoryRequest request;
	private Inventory inventory;
	private Inventory savedInventory;
	private InventoryResponse response;
	private UpdateInventoryRequest updateRequest;
	
	@BeforeEach
	void setUp() {
		// Arrange required objects
		request = new CreateInventoryRequest();
		request.setSkuCode("AIRPODS2USB");
		request.setQuantity(42);
		
		updateRequest = new UpdateInventoryRequest();
		updateRequest.setSkuCode("AIRPODS2USB");
		updateRequest.setQuantity(50);
		
		inventory = new Inventory();
		inventory.setSkuCode(request.getSkuCode());
		inventory.setQuantity(request.getQuantity());
		
		savedInventory = new Inventory();
		savedInventory.setId(1L);
		savedInventory.setSkuCode(request.getSkuCode());
		savedInventory.setQuantity(request.getQuantity());
		
		response = new InventoryResponse();
		response.setId(1L);
		response.setSkuCode(request.getSkuCode());
		response.setQuantity(request.getQuantity());
	}
	
	@Test
	void shouldSaveInventorySuccessfully() {
		// Mock behaviour
		when(inventoryRepository.existsBySkuCode(request.getSkuCode())).thenReturn(false);
		when(modelMapper.map(request, Inventory.class)).thenReturn(inventory);
		when(inventoryRepository.save(inventory)).thenReturn(savedInventory);
		when(modelMapper.map(savedInventory, InventoryResponse.class)).thenReturn(response);
		//Act
		InventoryResponse result = inventoryServiceImpl.saveInventory(request);
		//Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("AIRPODS2USB", result.getSkuCode());
		assertEquals(42, result.getQuantity());
		// verify
		verify(inventoryRepository).existsBySkuCode(request.getSkuCode());
		verify(modelMapper).map(request, Inventory.class);
		verify(inventoryRepository).save(inventory);
		verify(modelMapper).map(savedInventory, InventoryResponse.class);
	}
	
	@Test
	void shouldRejectDuplicateSkuDuringInventoryCreation() {
		// mock behaviour
		when(inventoryRepository.existsBySkuCode(request.getSkuCode())).thenReturn(true);
		// Act + Assert
		InventoryAlreadyExistsException exception = assertThrows(InventoryAlreadyExistsException.class, () -> inventoryServiceImpl.saveInventory(request));
		assertEquals("Inventory with SKU AIRPODS2USB already exists", exception.getMessage());
		// Verify
		verify(inventoryRepository, never()).save(any(Inventory.class));
		verify(modelMapper, never()).map(any(), eq(Inventory.class));
		verify(modelMapper, never()).map(any(), eq(InventoryResponse.class));
	}
	
	@Test
	void shouldReturnInventoryById() {
		// Mock behavior
		when(inventoryRepository.findById(savedInventory.getId())).thenReturn(Optional.of(savedInventory));
		when(modelMapper.map(savedInventory, InventoryResponse.class)).thenReturn(response);
		//Act
		InventoryResponse result = inventoryServiceImpl.getInventoryById(savedInventory.getId());
		//Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("AIRPODS2USB", result.getSkuCode());
		assertEquals(42, result.getQuantity());
		// verify
		verify(inventoryRepository).findById(savedInventory.getId());
		verify(modelMapper).map(savedInventory, InventoryResponse.class);
	}
	
	@Test
	void shouldThrowWhenInventoryIdDoesNotExist() {
		// mock behavior
		when(inventoryRepository.findById(savedInventory.getId())).thenReturn(Optional.empty());
		// Act + Assert
		InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class, () -> inventoryServiceImpl.getInventoryById(savedInventory.getId()));
		assertEquals("Inventory with ID "+ savedInventory.getId() + " not found", exception.getMessage());
		// verify
		verify(inventoryRepository).findById(savedInventory.getId());
		verifyNoInteractions(modelMapper);
	}
}
