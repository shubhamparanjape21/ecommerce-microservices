package com.japes.userservice.service;

import com.japes.userservice.dto.CreateUserRequest;
import com.japes.userservice.dto.LoginRequest;
import com.japes.userservice.dto.UserResponse;

public interface UserService {
	UserResponse createUser(CreateUserRequest request);
	UserResponse getUserById(Long id);
	UserResponse login(LoginRequest request);
}
