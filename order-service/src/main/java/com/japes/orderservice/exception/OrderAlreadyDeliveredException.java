package com.japes.orderservice.exception;

public class OrderAlreadyDeliveredException extends RuntimeException {
	public OrderAlreadyDeliveredException(String msg) {
		super(msg);
	}
}
