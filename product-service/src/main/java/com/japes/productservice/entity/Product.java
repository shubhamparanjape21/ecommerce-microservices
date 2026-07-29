package com.japes.productservice.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product extends BaseModel{
	
	@Column(nullable = false)
	private String name;
	
	@Column(length = 3000)
	private String description;
	
	private String brand;
	
	private String imageUrl;
	
	@Column(nullable = false)
	private boolean active = true;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductVariant> variants = new ArrayList<>();
}
