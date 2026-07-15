package com.japes.productservice.exception;

public class ProductAlreadyExistsException extends RuntimeException{
	
	public ProductAlreadyExistsException(String msg) {
		super(msg);
	}
}
