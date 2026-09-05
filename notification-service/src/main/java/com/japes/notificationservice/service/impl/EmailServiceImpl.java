package com.japes.notificationservice.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

}
