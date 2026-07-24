package com.japes.orderservice.exception;

public class InvalidOrderStatusTransitionException extends RuntimeException {
	public InvalidOrderStatusTransitionException(String msg) {
		super(msg);
	}
}
