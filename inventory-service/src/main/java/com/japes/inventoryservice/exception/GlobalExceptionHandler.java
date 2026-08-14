package com.japes.inventoryservice.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(InventoryAlreadyExistsException.class)
	public ResponseEntity<ApiError> handleInventoryAlreadyExistsException(InventoryAlreadyExistsException ex) {
		log.warn("Inventory already exists: {}", ex.getMessage());
		ApiError error = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for(FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		log.warn("Validation failed: {}", errors);
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now(), errors);
		return new ResponseEntity<ApiError>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InventoryNotFoundException.class)
	public ResponseEntity<ApiError> handleInventoryNotFoundException(InventoryNotFoundException ex) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InsufficientInventoryException.class)
	public ResponseEntity<ApiError> handleInsufficientInventoryExceptions(InsufficientInventoryException ex) {
		ApiError error = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllExceptions(Exception ex) {
		log.error("Unhandled exception occurred", ex);
		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
