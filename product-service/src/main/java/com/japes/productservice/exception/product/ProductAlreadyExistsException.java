package com.japes.productservice.exception.product;

public class ProductAlreadyExistsException extends RuntimeException{

	public ProductAlreadyExistsException(String msg) {
		super(msg);
	}
}
