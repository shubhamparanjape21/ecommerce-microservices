package com.japes.orderservice.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.japes.orderservice.dto.CreateOrderRequest;
import com.japes.orderservice.dto.OrderItemResponse;
import com.japes.orderservice.dto.OrderResponse;
import com.japes.orderservice.entity.Order;
import com.japes.orderservice.entity.OrderItem;
import com.japes.orderservice.enums.OrderStatus;
import com.japes.orderservice.exception.OrderNotFoundException;
import com.japes.orderservice.repository.OrderRepository;
import com.japes.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;

	@Override
	public OrderResponse placeOrder(CreateOrderRequest request) {
		log.info("Received request to place order for user {}", request.getUserId());
		
		Order order = new Order();
		order.setOrderNumber(generateOrderNumber());
		order.setUserId(request.getUserId());
		order.setStatus(OrderStatus.PENDING);
		
		log.debug("Mapping order items");
		List<OrderItem> orderItems = request.getItems()
				.stream()
				.map(itemRequest -> {
					OrderItem item = new OrderItem();
					item.setSkuCode(itemRequest.getSkuCode());
					item.setQuantity(itemRequest.getQuantity());
					/*
	                 * Temporary price.
	                 * Later we'll fetch actual product price
	                 * from Product Service using OpenFeign.
	                 */
					item.setPrice(BigDecimal.ZERO);
					item.setOrder(order);
					return item;
				})
				.toList();
		
		order.setOrderItems(orderItems);
		log.debug("Saving order to database");
		Order savedOrder = orderRepository.save(order);
		log.info("Successfully placed order {}", savedOrder.getOrderNumber());
		return mapToOrderResponse(savedOrder);
	}
	
	private String generateOrderNumber() {
		return "ORD-" + UUID.randomUUID()
				.toString()
				.substring(0, 8)
				.toUpperCase();
	}
	
	private OrderResponse mapToOrderResponse(Order order) {

	    List<OrderItemResponse> items = order.getOrderItems()
	            .stream()
	            .map(item -> new OrderItemResponse(
	                    item.getSkuCode(),
	                    item.getQuantity(),
	                    item.getPrice()))
	            .toList();

	    return new OrderResponse(
	            order.getId(),
	            order.getOrderNumber(),
	            order.getUserId(),
	            order.getStatus(),
	            items);
	}

	@Override
	public OrderResponse getOrderByOrderNumber(String orderNumber) {
		log.info("Received request to fetch order with order number {}", orderNumber);
	    log.debug("Checking whether order with order number {} exists", orderNumber);
	    Order order = orderRepository.findByOrderNumber(orderNumber)
	    		.orElseThrow(() -> {
	    			log.warn("Order not found with order number {}", orderNumber);
	    			return new OrderNotFoundException("Order with order number " + orderNumber + " not found");
	    		});
	    log.debug("Mapping Order entity to OrderResponse");
	    OrderResponse response = mapToOrderResponse(order);
	    log.info("Successfully fetched order with order number {}", orderNumber);
		return response;
	}

}
