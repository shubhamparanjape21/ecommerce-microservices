package com.japes.userservice.dto;

import com.japes.userservice.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
	private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
}
