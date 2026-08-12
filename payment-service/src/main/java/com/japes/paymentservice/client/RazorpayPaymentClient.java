package com.japes.paymentservice.client;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.razorpay.Order;
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
}
