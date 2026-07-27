package com.japes.productservice.exception.category;

public class CategoryAlreadyExistsException extends RuntimeException {
	public CategoryAlreadyExistsException(String msg) {
		super(msg);
	}
}
