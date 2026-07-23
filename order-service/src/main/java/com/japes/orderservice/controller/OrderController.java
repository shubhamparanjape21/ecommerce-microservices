package com.japes.orderservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.japes.orderservice.dto.CreateOrderRequest;
import com.japes.orderservice.dto.OrderPageResponse;
import com.japes.orderservice.dto.OrderResponse;
import com.japes.orderservice.dto.UpdateOrderStatusRequest;
import com.japes.orderservice.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	
	@PostMapping
	public ResponseEntity<OrderResponse> placeOrder(@RequestBody @Valid CreateOrderRequest request) {
		log.info("Received request to place order for user {}", request.getUserId());
		OrderResponse response = orderService.placeOrder(request);
		return new ResponseEntity<OrderResponse>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/{orderNumber}")
	public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
		log.info("Received request to fetch order {}", orderNumber);
		OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
		return new ResponseEntity<OrderResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<OrderPageResponse> getOrdersByUserId(
	        @PathVariable Long userId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "2") int size,
	        @RequestParam(defaultValue = "id") String sortBy,
	        @RequestParam(defaultValue = "desc") String direction) {

	    log.info("Received request to fetch orders for user {}", userId);

	    OrderPageResponse response =
	            orderService.getOrdersByUserId(userId, page, size, sortBy, direction);

	    return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{orderNumber}/status")
	public ResponseEntity<OrderResponse> updateOrderStatus(
			@PathVariable String orderNumber,
			@RequestBody @Valid UpdateOrderStatusRequest request){
		log.info("Received request to update status of order {}", orderNumber);
		OrderResponse response = orderService.updateOrderStatus(orderNumber, request);
		return ResponseEntity.ok(response);
	}
	
}
