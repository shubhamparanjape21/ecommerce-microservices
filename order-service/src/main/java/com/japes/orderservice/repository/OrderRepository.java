package com.japes.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
