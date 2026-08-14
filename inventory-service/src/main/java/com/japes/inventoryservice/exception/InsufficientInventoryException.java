package com.japes.inventoryservice.exception;

public class InsufficientInventoryException extends RuntimeException {
	public InsufficientInventoryException(String msg) {
		super(msg);
	}
}
