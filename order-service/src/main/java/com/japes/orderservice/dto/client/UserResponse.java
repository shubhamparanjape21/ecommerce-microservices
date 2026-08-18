package com.japes.orderservice.dto.client;

import com.japes.orderservice.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
	private Long id;
	private String name;
	private String email;
	private String phoneNumber;
	private Role role;
}
