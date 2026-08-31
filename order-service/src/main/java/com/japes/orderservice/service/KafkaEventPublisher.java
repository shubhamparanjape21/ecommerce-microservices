package com.japes.orderservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.japes.orderservice.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {

        log.info(
                "Publishing OrderCreatedEvent for order {}",
                event.orderNumber()
        );

        kafkaTemplate.send(
                ORDER_CREATED_TOPIC,
                event.orderNumber(),
                event
        );

        log.info(
                "OrderCreatedEvent published for order {}",
                event.orderNumber()
        );
    }
}
