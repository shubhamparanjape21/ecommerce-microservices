package com.japes.inventoryservice.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(String orderNumber, Long userId, BigDecimal totalAmount, List<OrderItemEvent> items) {

}
