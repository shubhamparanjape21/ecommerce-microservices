package com.japes.productservice.exception;

public class ValidationErrorResponse extends RuntimeException{

	public ValidationErrorResponse(String msg) {
		super(msg);
	}
}
