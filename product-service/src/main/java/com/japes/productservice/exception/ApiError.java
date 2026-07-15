package com.japes.productservice.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
	private Integer status;
	private String message;
	private LocalDateTime timestamp;
	private Map<String, String> errors;
}
