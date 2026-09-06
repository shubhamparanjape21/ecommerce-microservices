package com.japes.notificationservice.service;

import com.japes.notificationservice.event.OrderPaidEvent;

public interface EmailService {
	void sendWelcomeEmail(String email, String name);
	void sendOrderConfirmationEmail(OrderPaidEvent event);
}
