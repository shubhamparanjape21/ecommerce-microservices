package com.japes.productservice.exception.productvariant;

public class ProductVariantNotFoundException extends RuntimeException{
	public ProductVariantNotFoundException(String msg) {
		super(msg);
	}
}
