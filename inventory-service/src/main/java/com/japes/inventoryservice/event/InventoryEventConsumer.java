package com.japes.inventoryservice.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryEventConsumer {

    @KafkaListener(
        topics = "order-created",
        groupId = "inventory-service-v3"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {

        log.info(
            "Received OrderCreatedEvent for order {}",
            event.orderNumber()
        );
    }
}