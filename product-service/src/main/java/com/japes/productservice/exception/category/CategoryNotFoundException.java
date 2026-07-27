package com.japes.productservice.exception.category;

public class CategoryNotFoundException extends RuntimeException {
	public CategoryNotFoundException(String msg) {
		super(msg);
	}
}
