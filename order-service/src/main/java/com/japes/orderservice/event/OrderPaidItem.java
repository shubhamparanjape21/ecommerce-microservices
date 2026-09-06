package com.japes.orderservice.event;

import java.math.BigDecimal;

public record OrderPaidItem(String skuCode, Integer quantity, BigDecimal unitPrice, BigDecimal subTotal) {

}
