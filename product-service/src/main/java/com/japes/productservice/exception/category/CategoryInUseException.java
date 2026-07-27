package com.japes.productservice.exception.category;

public class CategoryInUseException extends RuntimeException{
	public CategoryInUseException(String msg) {
		super(msg);
	}
}
