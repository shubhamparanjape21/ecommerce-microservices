package com.japes.productservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageResponse {
	private List<ProductResponse> products;
	private int currentPage;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private boolean first;
	private boolean last;
}
