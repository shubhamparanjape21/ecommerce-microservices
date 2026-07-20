package com.japes.inventoryservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Paginated response containing inventory records")
public class InventoryPageResponse {
	@Schema(description = "List of inventory records")
	private List<InventoryResponse> inventories;
	@Schema(
	        description = "Current page number (zero-based)",
	        example = "0"
	    )
	private int currentPage;
	@Schema(
	        description = "Total number of available pages",
	        example = "5"
	    )
	private int totalPages;
	@Schema(
	        description = "Total number of inventory records",
	        example = "48"
	    )
	private long totalElements;
	@Schema(
	        description = "Number of records per page",
	        example = "10"
	    )
	private int pageSize;
	@Schema(
	        description = "Indicates whether this is the first page",
	        example = "true"
	    )
	private boolean isFirst;
	@Schema(
	        description = "Indicates whether this is the last page",
	        example = "false"
	    )
	private boolean isLast;
}
