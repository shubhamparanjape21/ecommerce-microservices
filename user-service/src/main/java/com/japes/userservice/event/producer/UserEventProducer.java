package com.japes.userservice.event.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.japes.userservice.event.UserRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {
	private static final String USER_REGISTERED_TOPIC = "user-registered";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {

        log.info(
            "Publishing UserRegisteredEvent for user {}",
            event.userId()
        );

        kafkaTemplate.send(
            USER_REGISTERED_TOPIC,
            event.userId().toString(),
            event
        );
    }
}
