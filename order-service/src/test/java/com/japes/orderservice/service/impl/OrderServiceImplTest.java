package com.japes.orderservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.japes.orderservice.dto.CreateOrderRequest;
import com.japes.orderservice.dto.OrderItemRequest;
import com.japes.orderservice.dto.OrderItemResponse;
import com.japes.orderservice.dto.OrderPageResponse;
import com.japes.orderservice.dto.OrderResponse;
import com.japes.orderservice.dto.UpdateOrderStatusRequest;
import com.japes.orderservice.entity.Order;
import com.japes.orderservice.entity.OrderItem;
import com.japes.orderservice.enums.OrderStatus;
import com.japes.orderservice.exception.InvalidOrderStatusTransitionException;
import com.japes.orderservice.exception.OrderAlreadyDeliveredException;
import com.japes.orderservice.exception.OrderNotFoundException;
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

		// Act
		OrderResponse result = orderServiceImpl.placeOrder(request);
		// No need to create OrderResponse because service creates it using
		// mapToOrderResponse()

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

	@Test
	void shouldGetOrderByOrderNumberSuccessfully() {
		// Mock
		when(orderRepository.findByOrderNumber(savedOrder.getOrderNumber())).thenReturn(Optional.of(savedOrder));
		// Act
		OrderResponse result = orderServiceImpl.getOrderByOrderNumber(savedOrder.getOrderNumber());
		// Assert
		assertNotNull(result);
		assertEquals(savedOrder.getId(), result.getId());
		assertEquals(savedOrder.getOrderNumber(), result.getOrderNumber());
		assertEquals(savedOrder.getUserId(), result.getUserId());
		assertEquals(savedOrder.getStatus(), result.getStatus());

		assertEquals(1, result.getItems().size());
		assertEquals("AIRPODS2USB", result.getItems().get(0).getSkuCode());
		assertEquals(2, result.getItems().get(0).getQuantity());

		// Verify
		verify(orderRepository).findByOrderNumber(savedOrder.getOrderNumber());
	}

	@Test
	void shouldThrowOrderNotFoundExceptionWhenOrderNumberDoesNotExist() {
		String orderNumber = "ORD-INVALID";
		// Mock
		when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

		// Act + Assert
		OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
				() -> orderServiceImpl.getOrderByOrderNumber(orderNumber));
		assertEquals("Order with order number " + orderNumber + " not found", exception.getMessage());

		// Verify
		verify(orderRepository).findByOrderNumber(orderNumber);
	}

	@Test
	void shouldGetOrdersByUserIdSuccessfully() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
		Page<Order> page = new PageImpl<>(List.of(savedOrder), pageable, 1);
		when(orderRepository.findOrderByUserId(1L, pageable)).thenReturn(page);
		// Act
		OrderPageResponse result = orderServiceImpl.getOrdersByUserId(1L, 0, 5, "id", "desc");
		// Assert
		assertNotNull(result);
		assertEquals(1, result.getOrders().size());
		assertEquals(0, result.getCurrentPage());
		assertEquals(5, result.getPageSize());
		assertEquals(1, result.getTotalElements());
		assertEquals(1, result.getTotalPages());

		assertTrue(result.isFirst());
		assertTrue(result.isLast());

		OrderResponse order = result.getOrders().get(0);

		assertEquals(savedOrder.getOrderNumber(), order.getOrderNumber());
		assertEquals(savedOrder.getUserId(), order.getUserId());
		assertEquals(savedOrder.getStatus(), order.getStatus());

		// Verify
		verify(orderRepository).findOrderByUserId(1L, pageable);
	}

	@Test
	void shouldReturnEmptyOrdersWhenUserHasNoOrders() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
		Page<Order> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
		when(orderRepository.findOrderByUserId(1L, pageable)).thenReturn(emptyPage);
		// Act
		OrderPageResponse result = orderServiceImpl.getOrdersByUserId(1L, 0, 5, "id", "desc");
		// Assert
		assertNotNull(result);
		assertTrue(result.getOrders().isEmpty());
		assertEquals(0, result.getCurrentPage());
		assertEquals(5, result.getPageSize());
		assertEquals(0, result.getTotalElements());
		assertEquals(0, result.getTotalPages());

		assertTrue(result.isFirst());
		assertTrue(result.isLast());

		// Verify
		verify(orderRepository).findOrderByUserId(1L, pageable);
	}

	@Test
	void shouldUpdateOrderStatusSuccessfully() {
		// prepare the request
		UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
		request.setStatus(OrderStatus.PAYMENT_PENDING);
		// Mock repository
		when(orderRepository.findByOrderNumber(savedOrder.getOrderNumber())).thenReturn(Optional.of(savedOrder));

		when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

		// Act
		OrderResponse result = orderServiceImpl.updateOrderStatus(savedOrder.getOrderNumber(), request);

		// Assert
		assertNotNull(result);

		assertEquals(savedOrder.getId(), result.getId());
		assertEquals(savedOrder.getOrderNumber(), result.getOrderNumber());
		assertEquals(savedOrder.getUserId(), result.getUserId());

		assertEquals(OrderStatus.PAYMENT_PENDING, result.getStatus());
		// Capture saved entity
		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

		verify(orderRepository).save(captor.capture());

		Order updatedOrder = captor.getValue();

		// Verify entity
		assertEquals(OrderStatus.PAYMENT_PENDING, updatedOrder.getStatus());

		assertEquals(savedOrder.getOrderNumber(), updatedOrder.getOrderNumber());

		assertEquals(savedOrder.getUserId(), updatedOrder.getUserId());

		// Verify repository interactions
		verify(orderRepository).findByOrderNumber(savedOrder.getOrderNumber());

		verify(orderRepository).save(any(Order.class));
	}

	@Test
	void shouldThrowInvalidOrderStatusTransitionException() {
		// Prepare the request
		UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
		request.setStatus(OrderStatus.SHIPPED);
		// Mock the repository
		when(orderRepository.findByOrderNumber(savedOrder.getOrderNumber())).thenReturn(Optional.of(savedOrder));
		// Act
		InvalidOrderStatusTransitionException exception = assertThrows(InvalidOrderStatusTransitionException.class,
				() -> orderServiceImpl.updateOrderStatus(savedOrder.getOrderNumber(), request));
		assertEquals("Cannot change order status from " + savedOrder.getStatus() + " to " + request.getStatus(),
				exception.getMessage());
		// Verify
		verify(orderRepository).findByOrderNumber(savedOrder.getOrderNumber());
		verify(orderRepository, never()).save(any(Order.class));
	}

	@Test
	void shouldCancelOrderSuccessfully() {
		// Mock
		when(orderRepository.findByOrderNumber(savedOrder.getOrderNumber())).thenReturn(Optional.of(savedOrder));

		when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

		// Act
		OrderResponse result = orderServiceImpl.cancelOrder(savedOrder.getOrderNumber());

		// Assert Response
		assertNotNull(result);
		assertEquals(savedOrder.getId(), result.getId());
		assertEquals(savedOrder.getOrderNumber(), result.getOrderNumber());
		assertEquals(savedOrder.getUserId(), result.getUserId());
		assertEquals(OrderStatus.CANCELLED, result.getStatus());

		// Capture saved Order
		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

		verify(orderRepository).save(orderCaptor.capture());

		Order cancelledOrder = orderCaptor.getValue();

		// Assert Entity
		assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());

		// Verify
		verify(orderRepository).findByOrderNumber(savedOrder.getOrderNumber());

		verify(orderRepository).save(any(Order.class));
	}

	@Test
	void shouldThrowOrderAlreadyDeliveredExceptionWhenCancellingDeliveredOrder() {
		// Arrange
		savedOrder.setStatus(OrderStatus.DELIVERED);
		// Mock
		when(orderRepository.findByOrderNumber(savedOrder.getOrderNumber())).thenReturn(Optional.of(savedOrder));
		// Act + Assert
		OrderAlreadyDeliveredException exception = assertThrows(OrderAlreadyDeliveredException.class,
				() -> orderServiceImpl.cancelOrder(savedOrder.getOrderNumber()));
		assertEquals("Delivered orders cannot be cancelled", exception.getMessage());
		// Verify
		verify(orderRepository).findByOrderNumber(savedOrder.getOrderNumber());

		verify(orderRepository, never()).save(any(Order.class));
	}

	@Test
	void shouldThrowOrderAlreadyCancelledExceptionWhenCancellingCancelledOrder() {
		// Arrange
		savedOrder.setStatus(OrderStatus.CANCELLED);
		// Mock
		when(orderRepository.findByOrderNumber(savedOrder.getOrderNumber())).thenReturn(Optional.of(savedOrder));
		// Act + Assert
		OrderAlreadyDeliveredException exception = assertThrows(OrderAlreadyDeliveredException.class,
				() -> orderServiceImpl.cancelOrder(savedOrder.getOrderNumber()));
		assertEquals("Order is already cancelled", exception.getMessage());
		// Verify
		verify(orderRepository).findByOrderNumber(savedOrder.getOrderNumber());

		verify(orderRepository, never()).save(any(Order.class));
	}
}
