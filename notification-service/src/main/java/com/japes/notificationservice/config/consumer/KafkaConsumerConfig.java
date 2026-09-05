package com.japes.notificationservice.config.consumer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.japes.notificationservice.event.UserRegisteredEvent;

@Configuration
public class KafkaConsumerConfig {
	@Bean
    public ConsumerFactory<String, UserRegisteredEvent> consumerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "localhost:9092"
        );

        config.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            "notification-service-v1"
        );

        config.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class
        );

        config.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            JacksonJsonDeserializer.class
        );

        config.put(
            "spring.json.value.default.type",
            UserRegisteredEvent.class
        );

        config.put(
            "spring.json.trusted.packages",
            "com.japes.notificationservice.event"
        );

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent>
            kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
