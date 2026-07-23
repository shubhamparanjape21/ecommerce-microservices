package com.japes.orderservice.exception;

public class OrderAlreadyCancelledException extends RuntimeException {
	public OrderAlreadyCancelledException(String msg) {
		super(msg);
	}
}
