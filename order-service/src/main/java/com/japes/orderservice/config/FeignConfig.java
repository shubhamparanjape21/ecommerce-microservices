package com.japes.orderservice.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {
	@Bean
    public RequestInterceptor correlationIdInterceptor() {

        return requestTemplate -> {

            String correlationId = MDC.get("X-Correlation-ID");

            if (correlationId != null && !correlationId.isBlank()) {
                requestTemplate.header(
                        "X-Correlation-ID",
                        correlationId
                );
            }
        };
    }
}
