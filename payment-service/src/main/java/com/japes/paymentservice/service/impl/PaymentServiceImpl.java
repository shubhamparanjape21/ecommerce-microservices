package com.japes.paymentservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.japes.paymentservice.client.RazorpayPaymentClient;
import com.japes.paymentservice.dto.CreatePaymentRequest;
import com.japes.paymentservice.dto.PaymentInitiationResponse;
import com.japes.paymentservice.dto.PaymentPageResponse;
import com.japes.paymentservice.dto.PaymentResponse;
import com.japes.paymentservice.dto.UpdatePaymentStatusRequest;
import com.japes.paymentservice.entity.Payment;
import com.japes.paymentservice.enums.PaymentStatus;
import com.japes.paymentservice.exception.InvalidPaymentStatusException;
import com.japes.paymentservice.exception.PaymentInitiationException;
import com.japes.paymentservice.exception.PaymentNotFoundException;
import com.japes.paymentservice.exception.PaymentRefundException;
import com.japes.paymentservice.repository.PaymentRepository;
import com.japes.paymentservice.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	
	private final PaymentRepository paymentRepository;
	private final RazorpayPaymentClient razorpayPaymentClient;
	
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

	@Override
	public PaymentResponse getPaymentByOrderNumber(String orderNumber) {
		log.debug("Searching payment by order number {}", orderNumber);
		
		Payment payment = paymentRepository.findByOrderNumber(orderNumber)
				.orElseThrow(() -> {
					log.warn("Payment not found for order {}", orderNumber);
					return new PaymentNotFoundException("Payment not found for order " + orderNumber);
				});
		
		log.info("Successfully fetched payment {} for order {}",
	            payment.getPaymentReference(),
	            orderNumber);
		
		return mapToPaymentResponse(payment);
	}
	
	@Override
	public PaymentResponse updatePaymentStatus(String paymentReference, UpdatePaymentStatusRequest request) {
		
		Payment payment = paymentRepository.findByPaymentReference(paymentReference)
				.orElseThrow(() -> {
					log.warn("Payment not found with reference {}", paymentReference);
					return new PaymentNotFoundException("Payment not found with reference {}" + paymentReference);
				});
		
		log.debug("Current status of payment {} is {}", paymentReference, payment.getPaymentStatus());

	    // Payment status can only be changed from PENDING
		if(payment.getPaymentStatus() != PaymentStatus.PENDING) {
			log.warn("Cannot update payment {} because current status is {}", paymentReference, payment.getPaymentStatus());

			throw new InvalidPaymentStatusException("Payment " + paymentReference + " cannot be updated because its current status is " + payment.getPaymentStatus());
		}
		
		PaymentStatus newStatus = request.getPaymentStatus();
		
		if(newStatus == PaymentStatus.PENDING) {
			log.warn("Invalid status update for payment {}: PENDING", paymentReference);

	        throw new InvalidPaymentStatusException("Payment status cannot be changed to PENDING");
		}
		
		// SUCCESS
		if(newStatus == PaymentStatus.SUCCESS) {
			if(request.getTransactionId() == null || request.getTransactionId().isBlank()) {
				log.warn("Transaction ID missing for successful payment {}", paymentReference);

	            throw new InvalidPaymentStatusException("Transaction ID is required for successful payment");
			}
			payment.setPaymentStatus(newStatus);
			payment.setTransactionId(request.getTransactionId());
			log.info("Payment {} marked SUCCESS with transaction ID {}", paymentReference, request.getTransactionId());
		} else if(newStatus == PaymentStatus.FAILED) { // FAILED
			payment.setPaymentStatus(newStatus);
			payment.setTransactionId(null);
			log.info("Payment {} marked FAILED", paymentReference);
		}
		
		Payment updatedPayment = paymentRepository.save(payment);
		log.info("Successfully updated payment {} to status {}", paymentReference, updatedPayment.getPaymentStatus());
		return mapToPaymentResponse(updatedPayment);
	}
	
	@Override
	public PaymentResponse refundPayment(String paymentReference) {
		log.info("Received request to refund payment {}", paymentReference);

	    Payment payment = paymentRepository.findByPaymentReference(paymentReference)
	            .orElseThrow(() -> {
	                log.warn("Payment not found with reference {}", paymentReference);

	                return new PaymentNotFoundException("Payment not found with reference " + paymentReference);
	            });

	    log.debug("Current status of payment {} is {}", paymentReference, payment.getPaymentStatus());

	    if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {

	        log.warn("Cannot refund payment {} because current status is {}", paymentReference, payment.getPaymentStatus());

	        throw new PaymentRefundException("Payment " + paymentReference + " cannot be refunded because its current status is " + payment.getPaymentStatus());
	    }
	    payment.setPaymentStatus(PaymentStatus.REFUNDED);
	    log.info("Payment {} marked as REFUNDED", paymentReference);
	    Payment refundedPayment = paymentRepository.save(payment);
	    log.info("Successfully refunded payment {}", paymentReference);
	    return mapToPaymentResponse(refundedPayment);
	}

	@Override
	public PaymentPageResponse getPaymentsByStatus(PaymentStatus status, int page, int pageSize, String sortBy, String direction) {
		log.info("Received request to fetch payments with status {} | page={}, size={}, sortBy={}, direction={}", status, page, pageSize, sortBy, direction);
		
		Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		
		log.debug("Fetching payments from database with pageable {}", pageable);
		
		Page<Payment> paymentPage = paymentRepository.findByPaymentStatus(status, pageable);
		
		List<PaymentResponse> payments = paymentPage.getContent()
				.stream()
				.map(this::mapToPaymentResponse)
				.toList();
		
		log.info("Successfully fetched {} payments with status {}", payments.size(), status);
		
		return new PaymentPageResponse(payments, paymentPage.getNumber(), paymentPage.getSize(), paymentPage.getTotalElements(), paymentPage.getTotalPages(), paymentPage.isFirst(), paymentPage.isLast());
	}

	@Override
	public PaymentInitiationResponse initiatePayment(String paymentReference) {
		log.info("Received request to initiate payment for {}", paymentReference);

	    Payment payment = paymentRepository.findByPaymentReference(paymentReference)
	            .orElseThrow(() -> {
	                log.warn("Payment not found for reference {}", paymentReference);

	                return new PaymentNotFoundException("Payment not found: " + paymentReference);
	            });

	    log.debug("Payment found. reference={}, status={}, amount={}", payment.getPaymentReference(), payment.getPaymentStatus(), payment.getAmount());

	    // Payment must be in PENDING state
	    if (payment.getPaymentStatus() != PaymentStatus.PENDING) {

	        log.warn("Cannot initiate payment {} because current status is {}", paymentReference, payment.getPaymentStatus());

	        throw new InvalidPaymentStatusException("Payment cannot be initiated in status: " + payment.getPaymentStatus());
	    }

	    try {

	        log.info("Creating Razorpay order for payment {}", paymentReference);

	        Order razorpayOrder = razorpayPaymentClient.createOrder(payment.getAmount(), payment.getPaymentReference());

	        String razorpayOrderId = razorpayOrder.get("id");

	        log.info("Razorpay order created successfully. paymentReference={}, razorpayOrderId={}", paymentReference, razorpayOrderId);

	        payment.setRazorpayOrderId(razorpayOrderId);

	        paymentRepository.save(payment);

	        log.info("Razorpay order ID saved for payment {}", paymentReference);

	        return new PaymentInitiationResponse(payment.getPaymentReference(), razorpayOrderId, payment.getAmount(), "INR", payment.getPaymentStatus().name()
	        );

	    } catch (RazorpayException ex) {

	        log.error("Razorpay payment initiation failed for payment {}", paymentReference, ex);

	        throw new PaymentInitiationException("Unable to initiate payment with Razorpay");
	    }
	}
}
