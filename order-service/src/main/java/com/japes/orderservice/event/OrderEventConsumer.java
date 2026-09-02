package com.japes.orderservice.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.japes.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {
	private final OrderService orderService;

    @KafkaListener(
        topics = "inventory-reserved",
        groupId = "order-service-v3"
    )
    public void consumeInventoryReserved(
            InventoryReservedEvent event) {

        log.info(
            "Received InventoryReservedEvent for order {}",
            event.orderNumber()
        );

        orderService.handleInventoryReserved(
            event.orderNumber()
        );
    }
}
