package com.japes.orderservice.entity;

import java.math.BigDecimal;
import java.util.List;

import com.japes.orderservice.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseModel{
	/**
     * Business identifier shown to customers.
     * Example: ORD-20260722-000001
     */
	@Column(nullable = false, unique = true)
	private String orderNumber;
	@Column(nullable = false)
	private Long userId;
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems;
	private boolean inventoryReserved = false;
	private String email;
}
