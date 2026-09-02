package com.japes.inventoryservice.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryEventProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	private static final String INVENTORY_RESERVED_TOPIC = "inventory-reserved";

    public void publishInventoryReserved(String orderNumber) {
        InventoryReservedEvent event = new InventoryReservedEvent(orderNumber);
        log.info("Publishing InventoryReservedEvent for order {}", orderNumber);
        kafkaTemplate.send(INVENTORY_RESERVED_TOPIC, orderNumber, event);
    }
}
