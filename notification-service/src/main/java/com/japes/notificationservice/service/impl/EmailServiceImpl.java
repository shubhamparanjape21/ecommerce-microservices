package com.japes.notificationservice.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.japes.notificationservice.event.OrderPaidEvent;
import com.japes.notificationservice.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
	private final JavaMailSender mailSender;

	@Override
	public void sendWelcomeEmail(String email, String name) {
		log.info("Sending welcome email to {}", email);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Welcome to our platform");
        message.setText(
                "Hi " + name + ",\n\n"
                + "Welcome! Your account has been created successfully.\n\n"
                + "Thank you for joining us."
        );

        mailSender.send(message);

        log.info("Welcome email sent successfully to {}", email);

	}

	@Override
	public void sendOrderConfirmationEmail(OrderPaidEvent event) {
		log.info(
	            "Sending order confirmation email for order {} to {}",
	            event.orderNumber(),
	            event.email()
	    );

	    SimpleMailMessage message = new SimpleMailMessage();

	    message.setTo(event.email());
	    message.setSubject("Order Confirmed - " + event.orderNumber());

	    StringBuilder emailBody = new StringBuilder();

	    emailBody.append("Hi,\n\n");
	    emailBody.append("Your payment was successful and your order has been confirmed.\n\n");

	    emailBody.append("Order Number: ")
	             .append(event.orderNumber())
	             .append("\n");

	    emailBody.append("Payment Status: ")
	             .append(event.paymentStatus())
	             .append("\n\n");

	    emailBody.append("Order Details:\n");
	    emailBody.append("--------------------------------\n");

	    for (OrderPaidEvent.OrderPaidItem item : event.items()) {

	        emailBody.append("SKU: ")
	                 .append(item.skuCode())
	                 .append("\n");

	        emailBody.append("Quantity: ")
	                 .append(item.quantity())
	                 .append("\n");

	        emailBody.append("Unit Price: ₹")
	                 .append(item.unitPrice())
	                 .append("\n");

	        emailBody.append("Subtotal: ₹")
	                 .append(item.subTotal())
	                 .append("\n");

	        emailBody.append("--------------------------------\n");
	    }

	    emailBody.append("Total Amount: ₹")
	             .append(event.totalAmount())
	             .append("\n\n");

	    emailBody.append("Thank you for your order!\n");

	    message.setText(emailBody.toString());

	    mailSender.send(message);

	    log.info(
	            "Order confirmation email sent successfully for order {}",
	            event.orderNumber()
	    );
		
	}

}
