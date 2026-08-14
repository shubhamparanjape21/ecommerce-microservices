package com.japes.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "APIs for managing customer orders")
public class OrderController {
	private final OrderService orderService;
	
	@Operation(summary = "Place a new order", description = "Creates a new order for a user with one or more order items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order request"),
            @ApiResponse(responseCode = "404", description = "Product or User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
	@PostMapping
	public ResponseEntity<OrderResponse> placeOrder(@RequestBody @Valid CreateOrderRequest request) {
		log.info("Received request to place order for user {}", request.getUserId());
		OrderResponse response = orderService.placeOrder(request);
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "Get order by order number", description = "Fetches order details using the unique order number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
	@GetMapping("/{orderNumber}")
	public ResponseEntity<OrderResponse> getOrderByOrderNumber(
			@Parameter(description = "Unique order number", example = "ORD-8A7F3D9B")
			@PathVariable String orderNumber) {
		log.info("Received request to fetch order {}", orderNumber);
		OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "Get orders by user", description = "Returns paginated orders for a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
	@GetMapping("/user/{userId}")
	public ResponseEntity<OrderPageResponse> getOrdersByUserId(
			@Parameter(description = "User ID", example = "1")
	        @PathVariable Long userId,
	        
	        @Parameter(description = "Page number", example = "0")
	        @RequestParam(defaultValue = "0") int page,
	        
	        @Parameter(description = "Page size", example = "10")
	        @RequestParam(defaultValue = "10") int size,
	        
	        @Parameter(description = "Sort field", example = "createdAt")
	        @RequestParam(defaultValue = "createdAt") String sortBy,
	        
	        @Parameter(description = "Sort direction", example = "desc")
	        @RequestParam(defaultValue = "desc") String direction) {

	    log.info("Received request to fetch orders for user {}", userId);

	    OrderPageResponse response =
	            orderService.getOrdersByUserId(userId, page, size, sortBy, direction);

	    return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "Update order status", description = "Updates the status of an existing order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
	@PutMapping("/status/{orderNumber}")
	public ResponseEntity<OrderResponse> updateOrderStatus(
			@Parameter(description = "Unique order number", example = "ORD-8A7F3D9B")
			@PathVariable String orderNumber,
			@RequestBody @Valid UpdateOrderStatusRequest request){
		log.info("Received request to update status of order {}", orderNumber);
		OrderResponse response = orderService.updateOrderStatus(orderNumber, request);
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "Cancel order", description = "Cancels an existing order if cancellation is allowed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled")
    })
	@PatchMapping("/cancel/{orderNumber}")
	public ResponseEntity<OrderResponse> cancelOrder(
			@Parameter(description = "Unique order number", example = "ORD-8A7F3D9B")
			@PathVariable String orderNumber){
		log.info("Received request to cancel order {}", orderNumber);
		OrderResponse response = orderService.cancelOrder(orderNumber);
		return ResponseEntity.ok(response);
	}
	
	@Operation(
	        summary = "Mark order payment as pending",
	        description = "Updates the payment status of the specified order to PAYMENT_PENDING."
	)
	@ApiResponses({
	        @ApiResponse(
	                responseCode = "204",
	                description = "Order payment marked as pending successfully"
	        ),
	        @ApiResponse(
	                responseCode = "404",
	                description = "Order not found"
	        )
	})
	@PutMapping("/payment-pending/{orderNumber}")
	public ResponseEntity<Void> markPaymentPending(
	        @Parameter(description = "Unique order number", example = "ORD-8A7F3D9B")
	        @PathVariable String orderNumber) {
	    orderService.markPaymentPending(orderNumber);
	    return ResponseEntity.noContent().build();
	}
	
	@Operation(
	        summary = "Handle successful payment",
	        description = "Marks the order as PAID after a successful payment, reduces inventory for all order items, and then moves the order to PROCESSING."
	)
	@ApiResponses({
	        @ApiResponse(
	                responseCode = "204",
	                description = "Payment processed successfully and order moved to PROCESSING"
	        ),
	        @ApiResponse(
	                responseCode = "404",
	                description = "Order not found"
	        ),
	        @ApiResponse(
	                responseCode = "409",
	                description = "Invalid order status transition"
	        ),
	        @ApiResponse(
	                responseCode = "400",
	                description = "Invalid order number"
	        )
	})
	@PutMapping("/payment-success/{orderNumber}")
	public ResponseEntity<Void> handleSuccessfulPayment(
	        @Parameter(
	                description = "Unique order number",
	                example = "ORD-20260814-1001",
	                required = true
	        )
	        @PathVariable String orderNumber) {

	    orderService.handleSuccessfulPayment(orderNumber);

	    return ResponseEntity.noContent().build();
	}
}
