package com.japes.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException(String msg) {
		super(msg);
	}
}
