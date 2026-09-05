package com.japes.notificationservice.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.japes.notificationservice.event.UserRegisteredEvent;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserRegisteredEventConsumer {
	@PostConstruct
    public void init() {
        log.info("UserRegisteredEventConsumer bean initialized");
    }
	@KafkaListener(
	        topics = "user-registered",
	        groupId = "notification-service-v1"
	    )
	    public void consumeUserRegistered(UserRegisteredEvent event) {

	        log.info(
	            "Received UserRegisteredEvent for user {} with email {}",
	            event.userId(),
	            event.email()
	        );
	    }
}
