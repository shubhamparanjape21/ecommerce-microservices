package com.japes.orderservice.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ApiError> handleInventoryAlreadyExistsException(OrderNotFoundException ex) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(OrderAlreadyDeliveredException.class)
	public ResponseEntity<ApiError> handleInventoryAlreadyExistsException(OrderAlreadyDeliveredException ex) {
		ApiError error = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(OrderAlreadyCancelledException.class)
	public ResponseEntity<ApiError> handleInventoryAlreadyExistsException(OrderAlreadyCancelledException ex) {
		ApiError error = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for(FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now(), errors);
		return new ResponseEntity<ApiError>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidOrderStatusTransitionException.class)
	public ResponseEntity<ApiError> handleInventoryNotFoundException(InvalidOrderStatusTransitionException ex) {
		ApiError error = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(FeignException.NotFound.class)
	public ResponseEntity<ApiError> handleFeignNotFound(FeignException.NotFound ex) {
	    ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "User not found", LocalDateTime.now(),Collections.emptyMap());
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllExceptions(Exception ex) {
		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), LocalDateTime.now());
		return new ResponseEntity<ApiError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
