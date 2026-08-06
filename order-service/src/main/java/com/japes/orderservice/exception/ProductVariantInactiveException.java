package com.japes.orderservice.exception;

public class ProductVariantInactiveException extends RuntimeException {
	public ProductVariantInactiveException(String msg) {
		super(msg);
	}
}
