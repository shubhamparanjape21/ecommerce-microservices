package com.japes.orderservice.event.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.japes.orderservice.event.OrderPaidEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventProducer {
	private static final String ORDER_PAID_TOPIC = "order-paid";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderPaid(OrderPaidEvent event) {

        log.info(
                "Publishing OrderPaidEvent for order {}",
                event.orderNumber()
        );

        kafkaTemplate.send(
                ORDER_PAID_TOPIC,
                event.orderNumber(),
                event
        );
    }
}
