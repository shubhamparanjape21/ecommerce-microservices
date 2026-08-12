package com.japes.paymentservice.entity;

import java.math.BigDecimal;

import com.japes.paymentservice.enums.PaymentMethod;
import com.japes.paymentservice.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment extends BaseModel {
	@Column(nullable = false, unique = true, length = 30)
	private String paymentReference;
	@Column(nullable = false, length = 30)
	private String orderNumber;
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentMethod paymentMethod;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentStatus paymentStatus;
	@Column(unique = true, length = 30)
	private String transactionId;
	@Column(unique = true, length = 50)
	private String razorpayOrderId;
}
