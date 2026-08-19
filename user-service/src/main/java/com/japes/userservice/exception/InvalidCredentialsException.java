package com.japes.userservice.exception;

public class InvalidCredentialsException extends RuntimeException {
	public InvalidCredentialsException(String msg) {
		super(msg);
	}
}
