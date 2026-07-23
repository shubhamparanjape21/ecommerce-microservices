package com.japes.orderservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPageResponse {
	private List<OrderResponse> orders;
	private int currentPage;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private boolean first;
	private boolean last;
}
