package com.japes.inventoryservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryPageResponse {
	private List<InventoryResponse> inventories;
	private int currentPage;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private boolean isFirst;
	private boolean isLast;
}
