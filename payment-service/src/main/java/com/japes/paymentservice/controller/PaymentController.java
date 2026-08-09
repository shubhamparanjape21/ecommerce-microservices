package com.japes.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.japes.paymentservice.dto.CreatePaymentRequest;
import com.japes.paymentservice.dto.PaymentResponse;
import com.japes.paymentservice.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(
	    name = "Payment Controller",
	    description = "APIs for managing customer payments"
	)
public class PaymentController {
	
	private final PaymentService paymentService;
	
	@Operation(
	        summary = "Create a payment",
	        description = "Creates a new payment for an order."
	    )
	    @ApiResponses(value = {
	        @ApiResponse(
	            responseCode = "201",
	            description = "Payment created successfully"
	        ),
	        @ApiResponse(
	            responseCode = "400",
	            description = "Invalid payment request"
	        ),
	        @ApiResponse(
	            responseCode = "404",
	            description = "Order not found"
	        ),
	        @ApiResponse(
	            responseCode = "500",
	            description = "Internal server error"
	        )
	    })
	@PostMapping
	public ResponseEntity<PaymentResponse> createPayment(@RequestBody @Valid CreatePaymentRequest request) {
		log.info("Received request to create payment for order {}",
                request.getOrderNumber());
		PaymentResponse response = paymentService.createPayment(request);
		log.info("Payment created successfully for order {}",
                request.getOrderNumber());
		return new ResponseEntity(response, HttpStatus.CREATED);
	}
	
	@Operation(
	        summary = "Get payment by reference",
	        description = "Retrieves payment details using the payment reference."
	)
	@ApiResponses(value = {
	        @ApiResponse(
	                responseCode = "200",
	                description = "Payment retrieved successfully"
	        ),
	        @ApiResponse(
	                responseCode = "404",
	                description = "Payment not found"
	        )
	})
	@GetMapping("/reference/{paymentReference}")
	public ResponseEntity<PaymentResponse> getPaymentByReference(
	        @PathVariable String paymentReference) {

	    log.info("Received request to fetch payment {}", paymentReference);

	    PaymentResponse response = paymentService.getPaymentByReference(paymentReference);

	    return ResponseEntity.ok(response);
	}
	
	@Operation(
	        summary = "Get payment by order number",
	        description = "Retrieves payment details using the order number."
	)
	@ApiResponses(value = {
	        @ApiResponse(
	                responseCode = "200",
	                description = "Payment retrieved successfully"
	        ),
	        @ApiResponse(
	                responseCode = "404",
	                description = "Payment not found for the order"
	        )
	})
	@GetMapping("/order/{orderNumber}")
	public ResponseEntity<PaymentResponse> getPaymentByOrderNumber(
	        @PathVariable String orderNumber) {

	    log.info("Received request to fetch payment for order {}", orderNumber);

	    PaymentResponse response = paymentService.getPaymentByOrderNumber(orderNumber);

	    return ResponseEntity.ok(response);
	}
	
}
