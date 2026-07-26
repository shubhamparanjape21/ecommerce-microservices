package com.japes.productservice.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category extends BaseModel{
	@Column(nullable = false, unique = true)
	private String name;
	
	@Column(length = 1000)
	private String description;
	
	@OneToMany(mappedBy = "category")
	private List<Product> products;
}
