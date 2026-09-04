package com.japes.orderservice.event;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProducer {
	private static final String INVENTORY_RELEASE_TOPIC =
            "inventory-release-requested";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInventoryReleaseRequested(
            String orderNumber,
            List<OrderItemEvent> items) {

        InventoryReleaseRequestedEvent event =
                new InventoryReleaseRequestedEvent(
                        orderNumber,
                        items
                );

        log.info(
            "Publishing InventoryReleaseRequestedEvent for order {}",
            orderNumber
        );

        kafkaTemplate.send(
            INVENTORY_RELEASE_TOPIC,
            orderNumber,
            event
        );
    }
}
