package com.japes.productservice.exception.productvariant;

public class ProductVariantInUseException extends RuntimeException {
	public ProductVariantInUseException(String msg) {
		super(msg);
	}
}
