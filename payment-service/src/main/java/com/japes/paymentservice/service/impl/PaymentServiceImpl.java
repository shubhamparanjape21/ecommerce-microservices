package com.japes.paymentservice.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.japes.paymentservice.dto.CreatePaymentRequest;
import com.japes.paymentservice.dto.PaymentResponse;
import com.japes.paymentservice.entity.Payment;
import com.japes.paymentservice.enums.PaymentStatus;
import com.japes.paymentservice.exception.PaymentNotFoundException;
import com.japes.paymentservice.repository.PaymentRepository;
import com.japes.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	
	private final PaymentRepository paymentRepository;

	@Override
	public PaymentResponse createPayment(CreatePaymentRequest request) {

        log.debug("Creating payment for order {} with amount {} and method {}",
                request.getOrderNumber(),
                request.getAmount(),
                request.getPaymentMethod());
        
        Payment payment = new Payment();
        
        payment.setPaymentReference(generatePaymentReference());
        payment.setOrderNumber(request.getOrderNumber());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        
        // Payment processing will be integrated with the payment gateway later
        payment.setPaymentStatus(PaymentStatus.PENDING);
        
        log.debug("Saving payment {} for order {}",
                payment.getPaymentReference(),
                payment.getOrderNumber());
        
        Payment savedPayment = paymentRepository.save(payment);
        
        log.info("Successfully created payment {} for order {} with status {}",
                savedPayment.getPaymentReference(),
                savedPayment.getOrderNumber(),
                savedPayment.getPaymentStatus());
        
        return mapToPaymentResponse(savedPayment);
	}
	
	private String generatePaymentReference() {
		return "PAY-" + UUID.randomUUID()
		.toString()
		.substring(0, 8)
		.toUpperCase();
	}
	
	private PaymentResponse mapToPaymentResponse(Payment payment) {
		return new PaymentResponse(
				payment.getId(),
				payment.getPaymentReference(),
				payment.getOrderNumber(),
				payment.getAmount(),
				payment.getPaymentMethod(),
				payment.getPaymentStatus(),
				payment.getTransactionId(),
				payment.getCreatedAt(),
				payment.getUpdatedAt()
				
		);
				
	}

	@Override
	public PaymentResponse getPaymentByReference(String paymentReference) {
		log.info("Received request to fetch payment {}", paymentReference);

	    log.debug("Searching payment by reference {}", paymentReference);
	    
		Payment payment = paymentRepository.findByPaymentReference(paymentReference)
				.orElseThrow(() -> {
					log.warn("Payment not found with reference {}", paymentReference);
					return new PaymentNotFoundException("Payment not found with reference " + paymentReference);
				});
		
		log.info("Successfully fetched payment {} for order {}",
	            payment.getPaymentReference(),
	            payment.getOrderNumber());
		return mapToPaymentResponse(payment);
	}

}
