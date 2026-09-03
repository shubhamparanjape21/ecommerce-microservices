package com.japes.orderservice.config;

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

import com.japes.orderservice.event.InventoryReservedEvent;

@Configuration
public class KafkaConsumerConfig {
	@Bean
    public ConsumerFactory<String, InventoryReservedEvent> consumerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "localhost:9092"
        );

        config.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            "order-service-v3"
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
            InventoryReservedEvent.class
        );

        config.put(
            "spring.json.trusted.packages",
            "com.japes.orderservice.event"
        );

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent>
            kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
