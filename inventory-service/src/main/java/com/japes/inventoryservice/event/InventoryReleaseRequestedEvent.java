package com.japes.inventoryservice.event;

import java.util.List;

public record InventoryReleaseRequestedEvent(String orderNumber, List<OrderItemEvent> items) {

}
