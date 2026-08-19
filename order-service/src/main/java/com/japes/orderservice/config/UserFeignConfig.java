package com.japes.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.japes.orderservice.exception.UserNotFoundException;

import feign.codec.ErrorDecoder;

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
}
