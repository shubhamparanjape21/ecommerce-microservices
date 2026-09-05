package com.japes.notificationservice.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.japes.notificationservice.event.UserRegisteredEvent;
import com.japes.notificationservice.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRegisteredEventConsumer {
	
	private final EmailService emailService;
	
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
	        
	        emailService.sendWelcomeEmail(event.email(), event.name());
	    }
}
