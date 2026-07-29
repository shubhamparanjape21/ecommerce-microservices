package com.japes.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.productservice.entity.VariantAttribute;

public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {

}
