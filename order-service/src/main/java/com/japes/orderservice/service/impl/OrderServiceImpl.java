package com.japes.orderservice.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.japes.orderservice.client.InventoryClient;
import com.japes.orderservice.client.PaymentClient;
import com.japes.orderservice.client.ProductClient;
import com.japes.orderservice.client.UserClient;
import com.japes.orderservice.dto.CreateOrderRequest;
import com.japes.orderservice.dto.OrderItemResponse;
import com.japes.orderservice.dto.OrderPageResponse;
import com.japes.orderservice.dto.OrderResponse;
import com.japes.orderservice.dto.UpdateOrderStatusRequest;
import com.japes.orderservice.dto.client.CreatePaymentRequest;
import com.japes.orderservice.dto.client.InventoryResponse;
import com.japes.orderservice.dto.client.PaymentInitiationResponse;
import com.japes.orderservice.dto.client.PaymentResponse;
import com.japes.orderservice.dto.client.ProductVariantResponse;
import com.japes.orderservice.dto.client.UserResponse;
import com.japes.orderservice.entity.Order;
import com.japes.orderservice.entity.OrderItem;
import com.japes.orderservice.enums.OrderStatus;
import com.japes.orderservice.event.InventoryEventProducer;
import com.japes.orderservice.event.OrderCreatedEvent;
import com.japes.orderservice.event.OrderItemEvent;
import com.japes.orderservice.exception.InsufficientInventoryException;
import com.japes.orderservice.exception.InvalidOrderStatusTransitionException;
import com.japes.orderservice.exception.OrderAlreadyDeliveredException;
import com.japes.orderservice.exception.OrderNotFoundException;
import com.japes.orderservice.exception.ProductVariantInactiveException;
import com.japes.orderservice.repository.OrderRepository;
import com.japes.orderservice.service.KafkaEventPublisher;
import com.japes.orderservice.service.OrderService;
import com.japes.orderservice.service.PaymentClientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final ProductClient productClient;
	private final InventoryClient inventoryClient;
	private final PaymentClient paymentClient;
	private final UserClient userClient;
	private final PaymentClientService paymentClientService;
	private final KafkaEventPublisher kafkaEventPublisher;
	private final InventoryEventProducer inventoryEventProducer;

	@Override
	public OrderResponse placeOrder(CreateOrderRequest request) {
		log.info("Received request to place order for user {}", request.getUserId());
		
		log.debug("Validating user {}", request.getUserId());
	    UserResponse user = userClient.getUserById(request.getUserId());

	    log.info("User {} validated successfully", user.getId());

		Order order = new Order();
		order.setOrderNumber(generateOrderNumber());
		order.setUserId(request.getUserId());
		order.setStatus(OrderStatus.PENDING);

		log.debug("Mapping order items");
		List<OrderItem> orderItems = request.getItems().stream().map(itemRequest -> {
			OrderItem item = new OrderItem();
			item.setSkuCode(itemRequest.getSkuCode());
			item.setQuantity(itemRequest.getQuantity());
			log.debug("Fetching product variant details for SKU {}", itemRequest.getSkuCode());
			ProductVariantResponse productVariant = productClient.getProductVariantBySkuCode(itemRequest.getSkuCode());
			log.debug("Validating whether product variant {} is active", itemRequest.getSkuCode());
			if(!productVariant.isActive()) {
				log.warn("Product variant {} is inactive", itemRequest.getSkuCode());
				throw new ProductVariantInactiveException("Product Variant " + itemRequest.getSkuCode() + " is inactive");
			}
			log.debug("Fetching inventory details for SKU {}", itemRequest.getSkuCode());
			InventoryResponse inventory = inventoryClient.getInventoryBySkuCode(itemRequest.getSkuCode());
			log.debug(
	                "Validating inventory for SKU {}. Available: {}, Requested: {}",
	                itemRequest.getSkuCode(),
	                inventory.getQuantity(),
	                itemRequest.getQuantity());
			if(inventory.getQuantity() < itemRequest.getQuantity()) {
				log.warn(
	                    "Insufficient stock for SKU {}. Available: {}, Requested: {}",
	                    itemRequest.getSkuCode(),
	                    inventory.getQuantity(),
	                    itemRequest.getQuantity());
				throw new InsufficientInventoryException("Insufficient stock with sku " + itemRequest.getSkuCode());
			}
			log.debug("Setting unit price {} for SKU {}",
	                productVariant.getPrice(),
	                itemRequest.getSkuCode());
			item.setUnitPrice(productVariant.getPrice());
			
			BigDecimal subTotal = productVariant.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
			log.debug("Calculated subtotal {} for SKU {}",
	                subTotal,
	                itemRequest.getSkuCode());
			item.setSubTotal(subTotal);
			item.setOrder(order);
			return item;
		}).toList();

		order.setOrderItems(orderItems);
		
		BigDecimal totalAmount = orderItems.stream()
				.map(OrderItem::getSubTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		order.setTotalAmount(totalAmount);
		log.debug("Saving order to database");
		Order savedOrder = orderRepository.save(order);
		log.info("Successfully placed order {}", savedOrder.getOrderNumber());
		List<OrderItemEvent> itemEvents = savedOrder.getOrderItems()
				.stream()
				.map(item -> new OrderItemEvent(
						item.getSkuCode(),
						item.getQuantity(),
						item.getUnitPrice(),
						item.getSubTotal()
						)).toList();
		OrderCreatedEvent event = new OrderCreatedEvent(savedOrder.getOrderNumber(), savedOrder.getUserId(), savedOrder.getTotalAmount(), itemEvents);
		// Publish event
		kafkaEventPublisher.publishOrderCreated(event);
		
	    // Create payment automatically

	    CreatePaymentRequest paymentRequest = new CreatePaymentRequest(savedOrder.getOrderNumber(), savedOrder.getTotalAmount(), request.getPaymentMethod());
	    log.info("Creating payment for order {} using payment method {}", savedOrder.getOrderNumber(), request.getPaymentMethod());
	    PaymentResponse paymentResponse = paymentClientService.createPayment(paymentRequest);
	    log.info("Payment {} created successfully for order {}", paymentResponse.getPaymentReference(), savedOrder.getOrderNumber());

	    // Initiate Razorpay payment automatically

	    log.info("Initiating payment {} for order {}", paymentResponse.getPaymentReference(), savedOrder.getOrderNumber());
	    PaymentInitiationResponse initiationResponse = paymentClientService.initiatePayment(paymentResponse.getPaymentReference());
	    log.info("Payment {} initiated successfully with Razorpay order {}", initiationResponse.getPaymentReference(), initiationResponse.getRazorpayOrderId());
	    return mapToOrderResponse(savedOrder);
	}

	private String generateOrderNumber() {
		return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private OrderResponse mapToOrderResponse(Order order) {

		List<OrderItemResponse> items = order.getOrderItems().stream()
				.map(item -> new OrderItemResponse(item.getSkuCode(), item.getQuantity(), item.getUnitPrice(), item.getSubTotal())).toList();

		return new OrderResponse(order.getId(), order.getOrderNumber(), order.getUserId(), order.getTotalAmount(), order.getStatus(), order.getCreatedAt(), order.getUpdatedAt(), items);
	}

	@Override
	public OrderResponse getOrderByOrderNumber(String orderNumber) {
		log.info("Received request to fetch order with order number {}", orderNumber);
		log.debug("Checking whether order with order number {} exists", orderNumber);
		Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> {
			log.warn("Order not found with order number {}", orderNumber);
			return new OrderNotFoundException("Order with order number " + orderNumber + " not found");
		});
		log.debug("Mapping Order entity to OrderResponse");
		OrderResponse response = mapToOrderResponse(order);
		log.info("Successfully fetched order with order number {}", orderNumber);
		return response;
	}

	@Override
	public OrderPageResponse getOrdersByUserId(Long userId, int page, int size, String sortBy, String direction) {
		log.info("Fetching orders for user ID {} - page: {}, size: {}, sortBy: {}, direction: {}", userId, page, size,
				sortBy, direction);

		Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Order> orderPage = orderRepository.findOrderByUserId(userId, pageable);
		log.debug("Retrieved {} orders from database", orderPage.getNumberOfElements());

		log.debug("Mapping Order entities to OrderResponse DTOs");
		List<OrderResponse> orders = orderPage.getContent().stream().map(this::mapToOrderResponse).toList();
		OrderPageResponse response = new OrderPageResponse(orders, orderPage.getNumber(), orderPage.getTotalPages(),
				orderPage.getTotalElements(), orderPage.getSize(), orderPage.isFirst(), orderPage.isLast());
		log.info("Successfully fetched {} orders for user ID {} (page {} of {})", orderPage.getNumberOfElements(),
				userId, orderPage.getNumber(), orderPage.getTotalPages());

		return response;
	}

	@Override
	public OrderResponse updateOrderStatus(String orderNumber, UpdateOrderStatusRequest request) {
		log.info("Received request to update status of order {}", orderNumber);
		log.debug("Checking whether order with order number {} exists", orderNumber);
		Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> {
			log.warn("Order not found with order number {}", orderNumber);
			return new OrderNotFoundException("Order with order number " + orderNumber + " not found");
		});
		OrderStatus currentStatus = order.getStatus();
		OrderStatus newStatus = request.getStatus();
		log.debug("Validating status transition from {} to {}", currentStatus, newStatus);
		if(!isValidStatusTransition(currentStatus, newStatus)) {
			log.warn("Invalid status transition from {} to {} for order {}",
		            currentStatus, newStatus, orderNumber);
			throw new InvalidOrderStatusTransitionException("Cannot change order status from " + currentStatus + " to " + newStatus);
		}
		log.debug("Updating order status from {} to {}", order.getStatus(), request.getStatus());
		order.setStatus(newStatus);
		log.debug("Saving updated order");
		Order updatedOrder = orderRepository.save(order);
		log.info("Successfully updated status of order {} to {}", orderNumber, updatedOrder.getStatus());
		return mapToOrderResponse(updatedOrder);
	}

	@Override
	public OrderResponse cancelOrder(String orderNumber) {
		log.info("Received request to cancel order {}", orderNumber);
		log.debug("Checking whether order with order number {} exists", orderNumber);
		Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> {
			log.warn("Order not found with order number {}", orderNumber);
			return new OrderNotFoundException("Order with order number " + orderNumber + " not found");
		});
		if (order.getStatus() == OrderStatus.DELIVERED) {
			log.warn("Cannot cancel order {} because it has already been delivered", orderNumber);
			throw new OrderAlreadyDeliveredException("Delivered orders cannot be cancelled");
		}
		if (order.getStatus() == OrderStatus.CANCELLED) {
			log.warn("Order {} is already cancelled", orderNumber);
			throw new OrderAlreadyDeliveredException("Order is already cancelled");
		}
		log.debug("Updating order status to CANCELED");
		order.setStatus(OrderStatus.CANCELLED);
		log.debug("Saving cancelled order");
		Order cancelledOrder = orderRepository.save(order);
		log.info("Successfully cancelled order {}", orderNumber);
		return mapToOrderResponse(cancelledOrder);
	}
	
	@Override
	@Transactional
	public void markPaymentPending(String orderNumber) {
		log.info("Marking order {} as PAYMENT_PENDING", orderNumber);

	    Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> {
	            log.warn("Order not found with order number {}", orderNumber);
	            return new OrderNotFoundException("Order not found: " + orderNumber);
	          });

	    log.debug("Order {} current status: {}", orderNumber, order.getStatus());
	    
	    OrderStatus currentStatus = order.getStatus();
		OrderStatus newStatus = OrderStatus.PAYMENT_PENDING;

//	    if (!isValidStatusTransition(currentStatus, newStatus)) {
//	    	log.warn("Invalid status transition from {} to {} for order {}", currentStatus, newStatus, orderNumber);
//	        throw new InvalidOrderStatusTransitionException("Cannot change order " + orderNumber + " from " + order.getStatus() + " to " + OrderStatus.PAYMENT_PENDING);
//	    }
		// Idempotent check
		if (currentStatus == newStatus) {
		    log.info(
		        "Order {} is already PAYMENT_PENDING. Skipping transition.",
		        orderNumber
		    );
		    return;
		}
	    order.setStatus(OrderStatus.PAYMENT_PENDING);
	    orderRepository.save(order);
	    log.info("Order {} successfully changed from PENDING to PAYMENT_PENDING", orderNumber);	
	}
	
	@Override
	@Transactional
	public void handleSuccessfulPayment(String orderNumber) {
	    log.info("Handling successful payment for order {}", orderNumber);

	    Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> {
	    	log.warn("Order not found with order number {}", orderNumber);
	    	return new OrderNotFoundException("Order not found: " + orderNumber);
	    });

	    log.debug("Order {} found with current status {} and inventoryReserved={}", orderNumber, order.getStatus(), order.isInventoryReserved());
 
	    /*
	     * Payment must be pending before marking it successful.
	     */
	    if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
	        log.warn("Cannot process successful payment for order {} because current status is {}", orderNumber, order.getStatus());
	        throw new InvalidOrderStatusTransitionException("Order cannot be marked as PAID from status " + order.getStatus());
	    }
	    /*
	     * PAYMENT_PENDING → PAID
	     */
	    if (!isValidStatusTransition(order.getStatus(), OrderStatus.PAID)) {
	        throw new InvalidOrderStatusTransitionException("Invalid order status transition from " + order.getStatus() + " to " + OrderStatus.PAID);
	    }
	    order.setStatus(OrderStatus.PAID);
	    /*
	     * If inventory was already reserved,
	     * both Saga steps are now complete.
	     *
	     * PAID → PROCESSING
	     */
	    if (order.isInventoryReserved()) {

	        if (!isValidStatusTransition(
	                order.getStatus(),
	                OrderStatus.PROCESSING)) {

	            throw new InvalidOrderStatusTransitionException(
	                "Invalid order status transition from "
	                + order.getStatus()
	                + " to "
	                + OrderStatus.PROCESSING
	            );
	        }

	        order.setStatus(OrderStatus.PROCESSING);

	        log.info(
	            "Payment successful and inventory already reserved for order {}. "
	            + "Status changed directly to PROCESSING",
	            orderNumber
	        );

	    } else {

	        log.info(
	            "Payment successful for order {}. "
	            + "Status changed to PAID. Waiting for inventory reservation.",
	            orderNumber
	        );
	    }
	    orderRepository.save(order);

	    //log.info("Order {} payment confirmed. Status changed to PAID", orderNumber);

	    /*
	     * Now consume inventory for every order item.
	     
	    for (OrderItem item : order.getOrderItems()) {
	        log.info("Reducing inventory for order {}. SKU={}, quantity={}", orderNumber, item.getSkuCode(), item.getQuantity());

	        // Synchronous flow 
	        //inventoryClient.reduceInventory(item.getSkuCode(), item.getQuantity());
	    }
	    */
	    /*
	     * All inventory reductions succeeded.
	     *
	     * PAID → PROCESSING
	     
	    if (!isValidStatusTransition(order.getStatus(), OrderStatus.PROCESSING)) {
	        throw new InvalidOrderStatusTransitionException("Invalid order status transition from " + order.getStatus() + " to " + OrderStatus.PROCESSING);
	    }

	    order.setStatus(OrderStatus.PROCESSING);
	    orderRepository.save(order);
	    log.info("Order {} processed successfully. Status changed to PROCESSING", orderNumber);
	    */
	}
	
	@Override
	@Transactional
	public void handleInventoryReserved(String orderNumber) {

	    log.info(
	        "Handling inventory reserved event for order {}",
	        orderNumber
	    );

	    Order order = orderRepository
	            .findByOrderNumber(orderNumber)
	            .orElseThrow(() -> {

	                log.warn(
	                    "Order not found with order number {}",
	                    orderNumber
	                );

	                return new OrderNotFoundException(
	                    "Order not found: " + orderNumber
	                );
	            });

	    log.debug(
	        "Order {} found with current status {}",
	        orderNumber,
	        order.getStatus()
	    );
	    
	    /*
	     * Idempotency:
	     * If inventory was already reserved, ignore duplicate event.
	     */
	    if (order.isInventoryReserved()) {

	        log.info(
	            "Inventory already reserved for order {}. Ignoring duplicate event.",
	            orderNumber
	        );

	        return;
	    }

	    /*
	     * Record that inventory has been reserved.
	     */
	    order.setInventoryReserved(true);

	    /*
	     *  Case 1:
	     * Payment is already successful.
	     *
	     * PAID → PROCESSING
	     */
	    if (order.getStatus() == OrderStatus.PAID) {
	    	
	    	if (!isValidStatusTransition(
	                order.getStatus(),
	                OrderStatus.PROCESSING)) {

	            throw new InvalidOrderStatusTransitionException(
	                "Invalid order status transition from "
	                + order.getStatus()
	                + " to "
	                + OrderStatus.PROCESSING
	            );
	        }

	        order.setStatus(OrderStatus.PROCESSING);

	        log.info(
	            "Payment already successful and inventory reserved for order {}. "
	            + "Status changed to PROCESSING",
	            orderNumber
	        );
	    } 
	    /*
	     * Case 2:
	     * Payment has not completed yet.
	     *
	     * Just remember that inventory is reserved.
	     * Payment event will complete the transition later.
	     */
	    else if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {

	        log.info(
	            "Inventory reserved for order {} but payment is still pending. "
	            + "Waiting for payment confirmation.",
	            orderNumber
	        );
	    }

	    /*
	     * Unexpected state.
	     */
	    else {

	        throw new InvalidOrderStatusTransitionException(
	            "Cannot process inventory reservation for order "
	            + orderNumber
	            + " from status "
	            + order.getStatus()
	        );
	    }
	    orderRepository.save(order);
	}
	
	@Override
	@Transactional
	public void handleFailedPayment(String orderNumber) {

	    log.info(
	        "Handling failed payment for order {}",
	        orderNumber
	    );

	    Order order = orderRepository
	            .findByOrderNumber(orderNumber)
	            .orElseThrow(() -> {

	                log.warn(
	                    "Order not found with order number {}",
	                    orderNumber
	                );

	                return new OrderNotFoundException(
	                    "Order not found: " + orderNumber
	                );
	            });

	    log.debug(
	        "Order {} found with current status {} and inventoryReserved={}",
	        orderNumber,
	        order.getStatus(),
	        order.isInventoryReserved()
	    );

	    /*
	     * Payment can fail only while payment is pending.
	     */
	    if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {

	        log.warn(
	            "Cannot process failed payment for order {} because current status is {}",
	            orderNumber,
	            order.getStatus()
	        );

	        throw new InvalidOrderStatusTransitionException(
	            "Order cannot be cancelled due to payment failure from status "
	            + order.getStatus()
	        );
	    }

	    /*
	     * PAYMENT_PENDING → CANCELLED
	     */
	    if (!isValidStatusTransition(
	            order.getStatus(),
	            OrderStatus.CANCELLED)) {

	        throw new InvalidOrderStatusTransitionException(
	            "Invalid order status transition from "
	            + order.getStatus()
	            + " to "
	            + OrderStatus.CANCELLED
	        );
	    }

	    order.setStatus(OrderStatus.CANCELLED);

	    orderRepository.save(order);

	    log.info(
	        "Payment failed for order {}. Status changed to CANCELLED. "
	        + "inventoryReserved={}",
	        orderNumber,
	        order.isInventoryReserved()
	    );
	    
	    /*
	     * Compensation:
	     * If inventory was already reserved,
	     * request Inventory Service to release it.
	     */
	    if (order.isInventoryReserved()) {

	        log.info(
	            "Inventory was already reserved for order {}. "
	            + "Requesting inventory release.",
	            orderNumber
	        );

	        List<OrderItemEvent> items = order.getOrderItems()
	                .stream()
	                .map(item -> new OrderItemEvent(
	                        item.getSkuCode(),
	                        item.getQuantity(),
	                        item.getUnitPrice(),
	                        item.getSubTotal()
	                ))
	                .toList();

	        inventoryEventProducer.publishInventoryReleaseRequested(
	                orderNumber,
	                items
	        );
	    }
	}

	private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {

		return switch (currentStatus) {

		case PENDING -> newStatus == OrderStatus.PAYMENT_PENDING || newStatus == OrderStatus.CANCELLED;

		case PAYMENT_PENDING -> newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;

		case PAID -> newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;

		case PROCESSING -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;

		case SHIPPED -> newStatus == OrderStatus.DELIVERED;

		case DELIVERED, CANCELLED -> false;
		};

	}
}
