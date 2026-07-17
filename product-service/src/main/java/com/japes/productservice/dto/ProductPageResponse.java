package com.japes.productservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Paginated response containing product details")
public class ProductPageResponse {
	@Schema(description = "List of products for the current page")
	private List<ProductResponse> products;
	@Schema(description = "Current page number (starts from 0)", example = "0")
	private int currentPage;
	@Schema(description = "Total number of available pages", example = "5")
	private int totalPages;
	@Schema(description = "Total number of products available", example = "48")
	private long totalElements;
	@Schema(description = "Number of products requested per page", example = "10")
	private int pageSize;
	@Schema(description = "Indicates whether this is the first page", example = "true")
	private boolean first;
	@Schema(description = "Indicates whether this is the last page", example = "false")
	private boolean last;
}
