package com.japes.inventoryservice.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.japes.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReleaseEventConsumer {
	private final InventoryService inventoryService;

    @KafkaListener(
        topics = "inventory-release-requested",
        groupId = "inventory-service-release-v1",
        containerFactory = "inventoryReleaseKafkaListenerContainerFactory"
    )
    public void consumeInventoryReleaseRequested(
            InventoryReleaseRequestedEvent event) {

        log.info(
            "Received InventoryReleaseRequestedEvent for order {}",
            event.orderNumber()
        );

        for (OrderItemEvent item : event.items()) {

            log.info(
                "Releasing inventory for order {}. SKU={}, quantity={}",
                event.orderNumber(),
                item.skuCode(),
                item.quantity()
            );

            inventoryService.releaseInventory(
                item.skuCode(),
                item.quantity()
            );
        }

        log.info(
            "Inventory successfully released for order {}",
            event.orderNumber()
        );
    }
}
