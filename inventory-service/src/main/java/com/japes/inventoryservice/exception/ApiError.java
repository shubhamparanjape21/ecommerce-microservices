package com.japes.inventoryservice.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ApiError {
	@NonNull
	private Integer status;
	@NonNull
	private String message;
	@NonNull
	private LocalDateTime timestamp;
	private Map<String, String> errors;
}
