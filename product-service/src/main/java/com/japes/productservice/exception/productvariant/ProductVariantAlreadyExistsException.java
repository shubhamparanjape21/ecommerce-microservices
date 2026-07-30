package com.japes.productservice.exception.productvariant;

public class ProductVariantAlreadyExistsException extends RuntimeException {
	public ProductVariantAlreadyExistsException(String msg) {
		super(msg);
	}
}
