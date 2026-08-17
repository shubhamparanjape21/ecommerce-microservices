package com.japes.userservice.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.japes.userservice.dto.CreateUserRequest;
import com.japes.userservice.dto.UserResponse;
import com.japes.userservice.entity.User;
import com.japes.userservice.exception.EmailAlreadyExistsException;
import com.japes.userservice.repository.UserRepository;
import com.japes.userservice.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	private UserResponse mapToUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }

	@Override
	@Transactional
	public UserResponse createUser(CreateUserRequest request) {
		log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("User registration failed. Email already exists: {}", request.getEmail());

            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = new User();

        BeanUtils.copyProperties(request, user);
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        log.info("User created successfully. User ID: {}", savedUser.getId());

        return mapToUserResponse(savedUser);
	}

}
