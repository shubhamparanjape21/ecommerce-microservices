package com.japes.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.japes.orderservice.dto.client.UserResponse;

@FeignClient(name = "user-service")
public interface UserClient {
	@GetMapping("/api/v1/users/{id}")
	UserResponse getUserById(@PathVariable Long id);
}
