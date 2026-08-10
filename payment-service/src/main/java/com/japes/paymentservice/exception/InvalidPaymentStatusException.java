package com.japes.paymentservice.exception;

public class InvalidPaymentStatusException extends RuntimeException {
	public InvalidPaymentStatusException(String msg) {
		super(msg);
	}
}
