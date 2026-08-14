package com.japes.paymentservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.japes.paymentservice.client.OrderClient;
import com.japes.paymentservice.client.RazorpayPaymentClient;
import com.japes.paymentservice.dto.CreatePaymentRequest;
import com.japes.paymentservice.dto.PaymentInitiationResponse;
import com.japes.paymentservice.dto.PaymentPageResponse;
import com.japes.paymentservice.dto.PaymentResponse;
import com.japes.paymentservice.dto.UpdatePaymentStatusRequest;
import com.japes.paymentservice.dto.VerifyPaymentRequest;
import com.japes.paymentservice.entity.Payment;
import com.japes.paymentservice.enums.PaymentStatus;
import com.japes.paymentservice.exception.InvalidPaymentStatusException;
import com.japes.paymentservice.exception.PaymentInitiationException;
import com.japes.paymentservice.exception.PaymentNotFoundException;
import com.japes.paymentservice.exception.PaymentRefundException;
import com.japes.paymentservice.exception.PaymentVerificationException;
import com.japes.paymentservice.repository.PaymentRepository;
import com.japes.paymentservice.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	
	private final PaymentRepository paymentRepository;
	private final RazorpayPaymentClient razorpayPaymentClient;
	private final OrderClient orderClient;
	
	@Value("${razorpay.key-id}")
	private String razorpayKeyId;
	
	@Value("${razorpay.key-secret}")
	private String razorpayKeySecret;
	
	@Value("${razorpay.webhook-secret}")
	private String razorpayWebhookSecret;
	
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
	        
	        log.info("Marking order {} as PAYMENT_PENDING", payment.getOrderNumber());

	        orderClient.markPaymentPending(payment.getOrderNumber());

	        return new PaymentInitiationResponse(payment.getPaymentReference(), razorpayOrderId, payment.getAmount(), "INR", payment.getPaymentStatus().name(), razorpayKeyId);

	    } catch (RazorpayException ex) {

	        log.error("Razorpay payment initiation failed for payment {}", paymentReference, ex);

	        throw new PaymentInitiationException("Unable to initiate payment with Razorpay");
	    }
	}

	@Override
	public PaymentResponse verifyPayment(VerifyPaymentRequest request) {
		log.info("Received request to verify Razorpay payment. orderId={}, paymentId={}", request.getRazorpayOrderId(), request.getRazorpayPaymentId());

	    try {
	    	JSONObject options = new JSONObject();

	    	options.put("razorpay_order_id", request.getRazorpayOrderId());
	    	options.put("razorpay_payment_id", request.getRazorpayPaymentId());
	    	options.put("razorpay_signature", request.getRazorpaySignature());

	    	boolean signatureValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

	        if (!signatureValid) {
	            log.warn("Invalid Razorpay signature for orderId={}, paymentId={}", request.getRazorpayOrderId(), request.getRazorpayPaymentId());
	            throw new PaymentVerificationException("Invalid Razorpay payment signature");
	        }

	        log.info("Razorpay payment signature verified successfully for orderId={}", request.getRazorpayOrderId());

	        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
	                .orElseThrow(() -> {
	                    log.warn("Payment not found for Razorpay order ID {}", request.getRazorpayOrderId());

	                    return new PaymentNotFoundException("Payment not found for Razorpay order ID: " + request.getRazorpayOrderId());
	                });

	        payment.setTransactionId(request.getRazorpayPaymentId());
	        payment.setPaymentStatus(PaymentStatus.SUCCESS);
	        Payment savedPayment = paymentRepository.save(payment);

	        log.info("Payment verified and updated successfully. paymentReference={}, transactionId={}, status={}",
	                savedPayment.getPaymentReference(),
	                savedPayment.getTransactionId(),
	                savedPayment.getPaymentStatus());

	        return mapToPaymentResponse(savedPayment);

	    } catch (RazorpayException ex) {
	        log.error("Razorpay payment verification failed for orderId={}", request.getRazorpayOrderId(), ex);
	        throw new PaymentVerificationException("Unable to verify Razorpay payment");
	    }
	}

	@Override
	public void handleWebhook(String payload, String signature) {
		log.info("Received Razorpay webhook");
	    try {
	        Utils.verifyWebhookSignature(payload, signature, razorpayWebhookSecret);
	       
	        log.info("Razorpay webhook signature verified successfully");
	        
	        JSONObject webhook = new JSONObject(payload);

	        String event = webhook.getString("event");

	        log.info("Received Razorpay webhook event: {}", event);

	        if (!event.equals("payment.captured") && !event.equals("payment.failed")) {
	            log.info("Ignoring unsupported Razorpay webhook event: {}", event);
	            return;
	        }

	        JSONObject paymentEntity = webhook
	                .getJSONObject("payload")
	                .getJSONObject("payment")
	                .getJSONObject("entity");

	        String razorpayPaymentId = paymentEntity.getString("id");

	        String razorpayOrderId = paymentEntity.getString("order_id");

	        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
	                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for Razorpay order ID: " + razorpayOrderId));

	        payment.setTransactionId(razorpayPaymentId);

	        if ("payment.captured".equals(event)) {
	            payment.setPaymentStatus(PaymentStatus.SUCCESS);
	            paymentRepository.save(payment);
	            log.info("Payment {} successful. Notifying Order Service for order {}", payment.getPaymentReference(), payment.getOrderNumber());
	            orderClient.handleSuccessfulPayment(payment.getOrderNumber());
	        } else {
	            payment.setPaymentStatus(PaymentStatus.FAILED);
	            paymentRepository.save(payment);
	            log.info("Payment {} failed for order {}", payment.getPaymentReference(), payment.getOrderNumber());
	        }
	        
	        log.info("Payment updated successfully through Razorpay webhook. reference={}, status={}", payment.getPaymentReference(), payment.getPaymentStatus());
	        
	    } catch (RazorpayException ex) {
	        log.error("Razorpay webhook signature verification failed", ex);
	        throw new PaymentVerificationException("Unable to verify Razorpay webhook");
	    }
		
	}
}
