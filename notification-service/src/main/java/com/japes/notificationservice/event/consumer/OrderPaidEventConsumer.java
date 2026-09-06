package com.japes.notificationservice.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.japes.notificationservice.event.OrderPaidEvent;
import com.japes.notificationservice.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderPaidEventConsumer {
	private final EmailService emailService;
	
	@KafkaListener(topics = "order-paid", groupId = "notification-service-v1")
	public void consumeOrderPaid(OrderPaidEvent event) {
		log.info(
	            "Received OrderPaidEvent for order {} with email {}",
	            event.orderNumber(),
	            event.email()
	        );
	}
}
