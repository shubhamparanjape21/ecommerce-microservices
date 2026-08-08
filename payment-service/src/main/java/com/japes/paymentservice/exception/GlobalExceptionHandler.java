package com.japes.paymentservice.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiError> handlePaymentNotFoundException(
            PaymentNotFoundException ex,
            WebRequest request) {

        log.warn("Payment not found: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }
}
