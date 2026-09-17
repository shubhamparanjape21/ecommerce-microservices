package com.japes.paymentservice.client;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RazorpayPaymentClient {
	
	private final RazorpayClient razorpayClient;

    public Order createOrder(
            BigDecimal amount,
            String paymentReference) throws RazorpayException {

        log.info(
                "Creating Razorpay order for payment {}",
                paymentReference);

        long amountInPaise = amount
                .movePointRight(2)
                .longValue();

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", paymentReference);

        Order razorpayOrder =
                razorpayClient.orders.create(orderRequest);

        log.info(
                "Razorpay order created successfully. paymentReference={}, razorpayOrderId={}",
                paymentReference,
                razorpayOrder.get("id"));

        return razorpayOrder;
    }
    
    // Used by the reconciliation job to ask Razorpay directly what happened to
    // an order when our webhook never arrived. This is a straight read against
    // Razorpay's API - it never writes anything, so it's safe to call as often
    // as reconciliation needs to.
    public List<Payment> fetchPaymentsForOrder(String razorpayOrderId) throws RazorpayException {
        log.info("Fetching payments from Razorpay for order {}", razorpayOrderId);
        List<Payment> payments = razorpayClient.orders.fetchPayments(razorpayOrderId);
        log.info("Razorpay returned {} payment attempt(s) for order {}", payments.size(), razorpayOrderId);
        return payments;
    }
}
