package com.japes.orderservice.event;

import java.math.BigDecimal;

public record OrderItemEvent(String skuCode, int quantity, BigDecimal unitPrice, BigDecimal subTotal) {

}
