package com.japes.orderservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByOrderNumber(String orderNumber);

}
