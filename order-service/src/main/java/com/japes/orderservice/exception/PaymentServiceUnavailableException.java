package com.japes.orderservice.exception;

public class PaymentServiceUnavailableException extends RuntimeException {
	public PaymentServiceUnavailableException(String msg) {
		super(msg);
	}
}
