package com.japes.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.japes.userservice.dto.CreateUserRequest;
import com.japes.userservice.dto.UserResponse;
import com.japes.userservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {
	private final UserService userService;

    @Operation(
        summary = "Create a new user",
        description = "Registers a new user"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already registered"
        )
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
