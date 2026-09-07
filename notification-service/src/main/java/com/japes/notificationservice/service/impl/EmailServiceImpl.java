package com.japes.notificationservice.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.japes.notificationservice.event.OrderPaidEvent;
import com.japes.notificationservice.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

	    try {

	        MimeMessage message = mailSender.createMimeMessage();

	        MimeMessageHelper helper =
	                new MimeMessageHelper(message, true, "UTF-8");

	        helper.setTo(event.email());

	        helper.setSubject(
	                "Order Confirmed - " + event.orderNumber()
	        );

	        StringBuilder itemsHtml = new StringBuilder();

	        for (OrderPaidEvent.OrderPaidItem item : event.items()) {

	            itemsHtml.append("""
	                    <tr>
	                        <td style="padding: 16px 0; border-bottom: 1px solid #eee;">
	                            <div style="font-size: 15px; font-weight: 600; color: #222;">
	                                %s
	                            </div>

	                            <div style="font-size: 13px; color: #777; margin-top: 5px;">
	                                Quantity: %d
	                            </div>
	                        </td>

	                        <td style="padding: 16px 0; border-bottom: 1px solid #eee;
	                                   text-align: right; vertical-align: top;
	                                   font-size: 15px; font-weight: 600; color: #222;">
	                            ₹%s
	                        </td>
	                    </tr>
	                    """.formatted(
	                    item.productName(),
	                    item.quantity(),
	                    item.subTotal()
	            ));
	        }

	        String html = """
	                <!DOCTYPE html>
	                <html>

	                <head>
	                    <meta charset="UTF-8">

	                    <meta name="viewport"
	                          content="width=device-width, initial-scale=1.0">
	                </head>

	                <body style="
	                    margin: 0;
	                    padding: 0;
	                    background-color: #f5f6f8;
	                    font-family: Arial, Helvetica, sans-serif;
	                    color: #222;
	                ">

	                    <div style="
	                        max-width: 600px;
	                        margin: 40px auto;
	                        background-color: #ffffff;
	                        border-radius: 12px;
	                        overflow: hidden;
	                        box-shadow: 0 4px 18px rgba(0,0,0,0.08);
	                    ">

	                        <!-- Header -->

	                        <div style="
	                            padding: 28px 32px;
	                            background-color: #111827;
	                            color: #ffffff;
	                            text-align: center;
	                        ">

	                            <div style="
	                                font-size: 24px;
	                                font-weight: bold;
	                                letter-spacing: 0.5px;
	                            ">
	                                Order Confirmed
	                            </div>

	                            <div style="
	                                margin-top: 8px;
	                                font-size: 14px;
	                                color: #d1d5db;
	                            ">
	                                Thank you for your purchase!
	                            </div>

	                        </div>


	                        <!-- Main Content -->

	                        <div style="padding: 32px;">

	                            <!-- Payment Status -->

	                            <div style="
	                                text-align: center;
	                                margin-bottom: 28px;
	                            ">

	                                <div style="
	                                    display: inline-block;
	                                    padding: 8px 16px;
	                                    background-color: #ecfdf5;
	                                    color: #047857;
	                                    border-radius: 20px;
	                                    font-size: 13px;
	                                    font-weight: bold;
	                                ">
	                                    ✓ PAYMENT SUCCESSFUL
	                                </div>

	                            </div>


	                            <!-- Order Information -->

	                            <table width="100%%"
	                                   cellpadding="0"
	                                   cellspacing="0"
	                                   style="margin-bottom: 28px;">

	                                <tr>

	                                    <td style="
	                                        font-size: 13px;
	                                        color: #777;
	                                        padding-bottom: 6px;
	                                    ">
	                                        Order Number
	                                    </td>

	                                    <td style="
	                                        text-align: right;
	                                        font-size: 14px;
	                                        font-weight: bold;
	                                        color: #222;
	                                        padding-bottom: 6px;
	                                    ">
	                                        %s
	                                    </td>

	                                </tr>

	                                <tr>

	                                    <td style="
	                                        font-size: 13px;
	                                        color: #777;
	                                    ">
	                                        Payment Status
	                                    </td>

	                                    <td style="
	                                        text-align: right;
	                                        font-size: 14px;
	                                        font-weight: bold;
	                                        color: #047857;
	                                    ">
	                                        %s
	                                    </td>

	                                </tr>

	                            </table>


	                            <!-- Order Summary -->

	                            <div style="
	                                font-size: 17px;
	                                font-weight: bold;
	                                margin-bottom: 10px;
	                            ">
	                                Order Summary
	                            </div>


	                            <table width="100%%"
	                                   cellpadding="0"
	                                   cellspacing="0">

	                                %s

	                            </table>


	                            <!-- Total -->

	                            <table width="100%%"
	                                   cellpadding="0"
	                                   cellspacing="0"
	                                   style="margin-top: 20px;">

	                                <tr>

	                                    <td style="
	                                        padding-top: 16px;
	                                        font-size: 17px;
	                                        font-weight: bold;
	                                    ">
	                                        Total
	                                    </td>

	                                    <td style="
	                                        padding-top: 16px;
	                                        text-align: right;
	                                        font-size: 20px;
	                                        font-weight: bold;
	                                    ">
	                                        ₹%s
	                                    </td>

	                                </tr>

	                            </table>


	                            <!-- Message -->

	                            <div style="
	                                margin-top: 32px;
	                                padding-top: 24px;
	                                border-top: 1px solid #eee;
	                                text-align: center;
	                                font-size: 14px;
	                                color: #666;
	                                line-height: 1.6;
	                            ">

	                                Your order has been successfully confirmed.

	                                <br>

	                                We'll keep you updated about your order.

	                            </div>

	                        </div>


	                        <!-- Footer -->

	                        <div style="
	                            padding: 20px 32px;
	                            background-color: #f9fafb;
	                            text-align: center;
	                            font-size: 12px;
	                            color: #999;
	                        ">

	                            Thank you for shopping with us.

	                        </div>

	                    </div>

	                </body>

	                </html>
	                """.formatted(
	                event.orderNumber(),
	                event.paymentStatus(),
	                itemsHtml,
	                event.totalAmount()
	        );


	        helper.setText(html, true);

	        mailSender.send(message);

	        log.info(
	                "Order confirmation email sent successfully for order {}",
	                event.orderNumber()
	        );

	    } catch (MessagingException e) {

	        log.error(
	                "Failed to send order confirmation email for order {}",
	                event.orderNumber(),
	                e
	        );

	        throw new RuntimeException(
	                "Failed to send order confirmation email",
	                e
	        );
	    }
	}
}
