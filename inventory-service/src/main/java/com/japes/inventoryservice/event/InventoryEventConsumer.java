package com.japes.inventoryservice.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.japes.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryEventConsumer {
	private final InventoryService inventoryService;

    @KafkaListener(
        topics = "order-created",
        groupId = "inventory-service-v3"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {

        log.info(
            "Received OrderCreatedEvent for order {}",
            event.orderNumber()
        );
        
        for (OrderItemEvent item : event.items()) {

            log.info(
                "Reserving inventory for SKU {} quantity {}",
                item.skuCode(),
                item.quantity()
            );

            inventoryService.reduceInventory(
                item.skuCode(),
                item.quantity()
            );
        }

        log.info(
            "Inventory successfully reserved for order {}",
            event.orderNumber()
        );
    }
}