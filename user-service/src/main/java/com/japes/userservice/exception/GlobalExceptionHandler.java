package com.japes.userservice.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage(), Collections.emptyMap());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for(FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		log.warn("Validation failed: {}", errors);
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage(), errors);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage(), Collections.emptyMap());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException ex) {
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), Collections.emptyMap());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllExceptions(Exception ex) {
		Map<String, String> errors = new HashMap<>();
		log.error("Unhandled exception occurred", ex);
		ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), errors);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
	    ApiError error = new ApiError(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Access denied", Map.of());
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}
}
