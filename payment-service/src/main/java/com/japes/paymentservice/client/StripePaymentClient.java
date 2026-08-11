package com.japes.paymentservice.client;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StripePaymentClient {
	private final StripeClient stripeClient;
	
	public PaymentIntent createPaymentIntent(BigDecimal amount, String paymentReference) throws StripeException {

        log.info("Creating Stripe PaymentIntent for payment {}", paymentReference);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(amount.movePointRight(2).longValue())
                        .setCurrency("inr")
                        .putMetadata("paymentReference", paymentReference)
                        .build();

        PaymentIntent paymentIntent = stripeClient.v1().paymentIntents().create(params);

        log.info("Stripe PaymentIntent created successfully. paymentReference={}, paymentIntentId={}", paymentReference, paymentIntent.getId());

        return paymentIntent;
	}
}
