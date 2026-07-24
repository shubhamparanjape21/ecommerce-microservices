package com.japes.orderservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.japes.orderservice.dto.CreateOrderRequest;
import com.japes.orderservice.dto.OrderItemRequest;
import com.japes.orderservice.dto.OrderItemResponse;
import com.japes.orderservice.dto.OrderResponse;
import com.japes.orderservice.entity.Order;
import com.japes.orderservice.entity.OrderItem;
import com.japes.orderservice.enums.OrderStatus;
import com.japes.orderservice.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private OrderServiceImpl orderServiceImpl;
	
	private CreateOrderRequest request;
	private Order order;
	private Order savedOrder;
	private OrderResponse response;
	
	@BeforeEach
	void setUp() {
		
		OrderItemRequest itemRequest = new OrderItemRequest();
	    itemRequest.setSkuCode("AIRPODS2USB");
	    itemRequest.setQuantity(2);

	    request = new CreateOrderRequest();
	    request.setUserId(1L);
	    request.setItems(List.of(itemRequest));

	    OrderItem savedItem = new OrderItem();
	    savedItem.setId(1L);
	    savedItem.setSkuCode("AIRPODS2USB");
	    savedItem.setQuantity(2);
	    savedItem.setPrice(BigDecimal.ZERO);

	    savedOrder = new Order();
	    savedOrder.setId(1L);
	    savedOrder.setOrderNumber("ORD-ABC12345");
	    savedOrder.setUserId(1L);
	    savedOrder.setStatus(OrderStatus.PENDING);
	    savedOrder.setOrderItems(List.of(savedItem));

	    savedItem.setOrder(savedOrder);
	}
	
	@Test
	void shouldPlaceOrderSuccessfully() {
		// Mock Behavior
		when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
		
		//Act
		OrderResponse result = orderServiceImpl.placeOrder(request);
		// No need to create OrderResponse because service creates it using mapToOrderResponse()
		
		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("ORD-ABC12345", result.getOrderNumber());
		assertEquals(1L, result.getUserId());
		assertEquals(OrderStatus.PENDING, result.getStatus());
		
		assertNotNull(result.getItems());
		assertEquals(1, result.getItems().size());
		
		OrderItemResponse item = result.getItems().get(0);
		
		assertEquals("AIRPODS2USB", item.getSkuCode());
		assertEquals(2, item.getQuantity());
		assertEquals(BigDecimal.ZERO, item.getPrice());
		
		// Capture the Order passed to repository.save()
	    ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

	    verify(orderRepository).save(orderCaptor.capture());

	    Order capturedOrder = orderCaptor.getValue();

	    // Assert the Order built by the service
	    assertNotNull(capturedOrder);
	    assertNotNull(capturedOrder.getOrderNumber());
	    assertTrue(capturedOrder.getOrderNumber().startsWith("ORD-"));

	    assertEquals(1L, capturedOrder.getUserId());
	    assertEquals(OrderStatus.PENDING, capturedOrder.getStatus());

	    assertNotNull(capturedOrder.getOrderItems());
	    assertEquals(1, capturedOrder.getOrderItems().size());

	    OrderItem capturedItem = capturedOrder.getOrderItems().get(0);

	    assertEquals("AIRPODS2USB", capturedItem.getSkuCode());
	    assertEquals(2, capturedItem.getQuantity());
	    assertEquals(BigDecimal.ZERO, capturedItem.getPrice());

	    // Verify bidirectional relationship
	    assertSame(capturedOrder, capturedItem.getOrder());
	}
}
