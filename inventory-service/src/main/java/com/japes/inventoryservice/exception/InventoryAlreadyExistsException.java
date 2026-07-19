package com.japes.inventoryservice.exception;

public class InventoryAlreadyExistsException extends RuntimeException{
	public InventoryAlreadyExistsException(String msg) {
		super(msg);
	}
}
