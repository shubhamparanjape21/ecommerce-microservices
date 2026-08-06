package com.japes.orderservice.exception;

public class InsufficientInventoryException extends RuntimeException {
	public InsufficientInventoryException(String msg) {
		super(msg);
	}
}
