package com.japes.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.StripeClient;

@Configuration
public class StripeConfig {
	@Value("${stripe.secret-key}")
	private String stripeSecretKey;
	
	public StripeClient stripeClient() {
		return new StripeClient(stripeSecretKey);
	}
}
