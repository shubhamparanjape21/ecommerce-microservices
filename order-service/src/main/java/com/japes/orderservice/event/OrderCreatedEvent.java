package com.japes.orderservice.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(String OrderNumber, Long userId, BigDecimal totalAmount, List<OrderItemEvent> items) {

}
