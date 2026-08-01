package com.japes.productservice.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.japes.productservice.dto.productvariant.CreateProductVariantRequest;
import com.japes.productservice.dto.productvariant.ProductVariantResponse;
import com.japes.productservice.dto.productvariant.UpdateProductVariantRequest;
import com.japes.productservice.dto.productvariant.VariantAttributeRequest;
import com.japes.productservice.dto.productvariant.VariantAttributeResponse;
import com.japes.productservice.entity.Product;
import com.japes.productservice.entity.ProductVariant;
import com.japes.productservice.entity.VariantAttribute;
import com.japes.productservice.repository.ProductRepository;
import com.japes.productservice.repository.ProductVariantRepository;

@ExtendWith(MockitoExtension.class)
public class ProductVariantServiceImplTest {
	@Mock
	private ProductVariantRepository productVariantRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private ProductVariantServiceImpl productVariantServiceImpl;
	
	private Product product;
	private ProductVariant productVariant;
	private VariantAttribute variantAttribute;
	private CreateProductVariantRequest createRequest;
	private UpdateProductVariantRequest updateRequest;
	private VariantAttributeRequest attributeRequest;
	private ProductVariantResponse response;
	
	@BeforeEach
	void setUp() {
	    product = new Product();
	    product.setId(1L);
	    product.setName("iPhone 16");

	    attributeRequest = new VariantAttributeRequest();
	    attributeRequest.setAttributeName("Storage");
	    attributeRequest.setAttributeValue("128 GB");

	    createRequest = new CreateProductVariantRequest();
	    createRequest.setProductId(1L);
	    createRequest.setSkuCode("IPH16-128-BLK");
	    createRequest.setPrice(new BigDecimal("79999"));
	    createRequest.setAttributes(List.of(attributeRequest));

	    updateRequest = new UpdateProductVariantRequest();
	    updateRequest.setSkuCode("IPH16-256-BLK");
	    updateRequest.setPrice(new BigDecimal("89999"));
	    updateRequest.setActive(true);
	    updateRequest.setAttributes(List.of(attributeRequest));

	    variantAttribute = new VariantAttribute();
	    variantAttribute.setId(1L);
	    variantAttribute.setAttributeName("Storage");
	    variantAttribute.setAttributeValue("128 GB");

	    productVariant = new ProductVariant();
	    productVariant.setId(1L);
	    productVariant.setProduct(product);
	    productVariant.setSkuCode("IPH16-128-BLK");
	    productVariant.setPrice(new BigDecimal("79999"));
	    productVariant.setActive(true);

	    variantAttribute.setProductVariant(productVariant);

	    productVariant.setAttributes(List.of(variantAttribute));

	    response = new ProductVariantResponse();
	    response.setId(1L);
	    response.setProductId(1L);
	    response.setProductName("iPhone 16");
	    response.setSkuCode("IPH16-128-BLK");
	    response.setPrice(new BigDecimal("79999"));
	    response.setActive(true);

	    VariantAttributeResponse attributeResponse = new VariantAttributeResponse();
	    attributeResponse.setId(1L);
	    attributeResponse.setAttributeName("Storage");
	    attributeResponse.setAttributeValue("128 GB");

	    response.setAttributes(List.of(attributeResponse));
	}
}
