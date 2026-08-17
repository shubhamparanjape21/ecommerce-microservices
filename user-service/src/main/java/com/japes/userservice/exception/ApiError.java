package com.japes.userservice.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {
	private LocalDateTime timestamp;
    private int status;
    private String message;
    private Map<String, String> errors;
}
