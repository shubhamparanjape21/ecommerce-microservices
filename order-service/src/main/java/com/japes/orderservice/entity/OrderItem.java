package com.japes.orderservice.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseModel{
	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;
	@Column(nullable = false)
	private String skuCode; // SKU received from product service
	@Column(nullable = false)
	private Integer quantity; // Quantity ordered
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice; // Price at the time of placing order - should never change even if product price changes later
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal subTotal;
}
