package com.japes.userservice.exception;

public class EmailAlreadyExistsException extends RuntimeException{
	public EmailAlreadyExistsException(String msg) {
		super(msg);
	}
}
