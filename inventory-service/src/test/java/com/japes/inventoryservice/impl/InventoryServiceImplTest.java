package com.japes.inventoryservice.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.japes.inventoryservice.dto.CreateInventoryRequest;
import com.japes.inventoryservice.dto.InventoryPageResponse;
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
		when(inventoryRepository.save(inventory)).thenReturn(savedInventory);
		when(modelMapper.map(savedInventory, InventoryResponse.class)).thenReturn(response);
		// Act
		InventoryResponse result = inventoryServiceImpl.saveInventory(request);
		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("AIRPODS2USB", result.getSkuCode());
		assertEquals(42, result.getQuantity());
		ArgumentCaptor<Inventory> captor =
		        ArgumentCaptor.forClass(Inventory.class);

		verify(inventoryRepository).save(captor.capture());

		Inventory saved = captor.getValue();

		assertEquals(request.getSkuCode(), saved.getSkuCode());
		assertEquals(request.getQuantity(), saved.getQuantity());
		// verify
		verify(inventoryRepository).existsBySkuCode(request.getSkuCode());
		verify(inventoryRepository).save(inventory);
		verify(modelMapper).map(savedInventory, InventoryResponse.class);
	}

	@Test
	void shouldRejectDuplicateSkuDuringInventoryCreation() {
		// mock behaviour
		when(inventoryRepository.existsBySkuCode(request.getSkuCode())).thenReturn(true);
		// Act + Assert
		InventoryAlreadyExistsException exception = assertThrows(InventoryAlreadyExistsException.class,
				() -> inventoryServiceImpl.saveInventory(request));
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
		// Act
		InventoryResponse result = inventoryServiceImpl.getInventoryById(savedInventory.getId());
		// Assert
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
		InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class,
				() -> inventoryServiceImpl.getInventoryById(savedInventory.getId()));
		assertEquals("Inventory with ID " + savedInventory.getId() + " not found", exception.getMessage());
		// verify
		verify(inventoryRepository).findById(savedInventory.getId());
		verifyNoInteractions(modelMapper);
	}

	@Test
	void shouldReturnInventoryBySku() {
		// Mock behavior
		when(inventoryRepository.findBySkuCode(savedInventory.getSkuCode())).thenReturn(Optional.of(savedInventory));
		when(modelMapper.map(savedInventory, InventoryResponse.class)).thenReturn(response);
		// Act
		InventoryResponse result = inventoryServiceImpl.getInventoryBySkuCode(savedInventory.getSkuCode());
		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("AIRPODS2USB", result.getSkuCode());
		assertEquals(42, result.getQuantity());
		// verify
		verify(inventoryRepository).findBySkuCode(savedInventory.getSkuCode());
		verify(modelMapper).map(savedInventory, InventoryResponse.class);
	}

	@Test
	void shouldThrowWhenInventorySkuDoesNotExist() {
		// mock behavior
		when(inventoryRepository.findBySkuCode(savedInventory.getSkuCode())).thenReturn(Optional.empty());
		// Act + Assert
		InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class,
				() -> inventoryServiceImpl.getInventoryBySkuCode(savedInventory.getSkuCode()));
		assertEquals("Inventory with SKU " + savedInventory.getSkuCode() + " not found", exception.getMessage());
		// verify
		verify(inventoryRepository).findBySkuCode(savedInventory.getSkuCode());
		verifyNoInteractions(modelMapper);
	}

	@Test
	void shouldReturnPaginatedInventoryList() {

		Inventory inventory2 = new Inventory();
		inventory.setId(2L);
		inventory2.setSkuCode("DELLU2724D");
		inventory2.setQuantity(20);

		InventoryResponse response2 = new InventoryResponse();
		response2.setId(inventory2.getId());
		response2.setSkuCode(inventory2.getSkuCode());
		response2.setQuantity(inventory2.getQuantity());

		List<Inventory> inventories = List.of(savedInventory, inventory2);
		Page<Inventory> inventoryPage = new PageImpl<>(inventories); // it simulates to
																		// productRepository.findAll(pageable);
		// mock behavior
		when(inventoryRepository.findAll(any(Pageable.class))).thenReturn(inventoryPage);
		when(modelMapper.map(savedInventory, InventoryResponse.class)).thenReturn(response);
		when(modelMapper.map(inventory2, InventoryResponse.class)).thenReturn(response2);
		// Act
		InventoryPageResponse result = inventoryServiceImpl.getInventoryList(0, 5, "name", "asc");
		// Assert
		assertNotNull(result);
		assertEquals(2, result.getInventories().size());
		assertEquals(0, result.getCurrentPage());
		assertEquals(2, result.getTotalElements());
		assertTrue(result.isFirst());
		assertTrue(result.isLast());
		// verify
		verify(inventoryRepository).findAll(any(Pageable.class));
		verify(modelMapper).map(savedInventory, InventoryResponse.class);
		verify(modelMapper).map(inventory2, InventoryResponse.class);
	}

	@Test
	void shouldUpdateInventorySuccessfully() {
		// mock behavior
		when(inventoryRepository.findById(savedInventory.getId())).thenReturn(Optional.of(savedInventory));
		// manually change the object state to represent its updated state
		savedInventory.setQuantity(updateRequest.getQuantity());
		// stub
		when(inventoryRepository.save(savedInventory)).thenReturn(savedInventory);
		when(modelMapper.map(savedInventory, InventoryResponse.class)).thenReturn(response);
		// update response
		response.setQuantity(updateRequest.getQuantity());
		// Act
		InventoryResponse result = inventoryServiceImpl.updateInventory(1L, updateRequest);
		// Assert
		assertNotNull(result);
		assertEquals(updateRequest.getQuantity(), result.getQuantity());
		ArgumentCaptor<Inventory> captor =
		        ArgumentCaptor.forClass(Inventory.class);

		verify(inventoryRepository).save(captor.capture());

		Inventory updated = captor.getValue();

		assertEquals(updateRequest.getQuantity(), updated.getQuantity());
		// verify
		verify(inventoryRepository).findById(savedInventory.getId());
		verify(inventoryRepository).save(savedInventory);
		verify(modelMapper).map(savedInventory, InventoryResponse.class);
	}

	@Test
	void shouldThrowWhenUpdatingNonExistingInventory() {
		when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());
		InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class,
				() -> inventoryServiceImpl.updateInventory(1L, updateRequest));
		assertEquals("Inventory with ID 1 not found", exception.getMessage());
		verify(inventoryRepository).findById(1L);
		verify(inventoryRepository, never()).save(any(Inventory.class));
		verifyNoInteractions(modelMapper);
	}

	@Test
	void shouldDeleteInventorySuccessfully() {
		// mocking behaviour
		when(inventoryRepository.findById(savedInventory.getId())).thenReturn(Optional.of(savedInventory));
		// Act
		inventoryServiceImpl.deleteInventory(savedInventory.getId());
		// Assert - nothing to assert because no return value
		// verify interactions
		verify(inventoryRepository).findById(savedInventory.getId());
		verify(inventoryRepository).delete(savedInventory);
	}

	@Test
	void shouldThrowWhenDeletingNonExistingInventory() {
		// mock the repo
		when(inventoryRepository.findById(savedInventory.getId())).thenReturn(Optional.empty());
		// Act + Assert
		InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class,
				() -> inventoryServiceImpl.deleteInventory(savedInventory.getId()));
		assertEquals("Inventory with ID "+ savedInventory.getId() +" not found", exception.getMessage());
		// verify repository searched
		verify(inventoryRepository).findById(savedInventory.getId());
		// verify delete should never happen
		verify(inventoryRepository, never()).deleteById(anyLong());
	}
}
