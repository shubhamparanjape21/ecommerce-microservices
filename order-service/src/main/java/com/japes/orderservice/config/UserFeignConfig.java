package com.japes.orderservice.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.japes.orderservice.exception.UserNotFoundException;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class UserFeignConfig {
	
	@Bean
	public ErrorDecoder userErrorDecoder() {
		return (methodKey, response) -> {
			if(response.status() == 404) {
				return new UserNotFoundException("User not found");
			}
			
			return new RuntimeException("User service returned error: " + response.status());
		};
	}
	
	@Bean
    public RequestInterceptor userRequestInterceptor() {

        return requestTemplate -> {

            // Forward Correlation ID
            String correlationId = MDC.get("X-Correlation-ID");

            if (correlationId != null && !correlationId.isBlank()) {
                requestTemplate.header(
                        "X-Correlation-ID",
                        correlationId
                );
            }

            // Forward JWT
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes)
                            RequestContextHolder.getRequestAttributes();

            if (attributes != null) {

                HttpServletRequest request =
                        attributes.getRequest();

                String authorization =
                        request.getHeader("Authorization");

                if (authorization != null && !authorization.isBlank()) {
                    requestTemplate.header(
                            "Authorization",
                            authorization
                    );
                }
            }
        };
	}
}
