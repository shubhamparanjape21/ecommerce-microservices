package com.japes.productservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.japes.productservice.exception.product.ProductNotFoundException;
import com.japes.productservice.exception.productvariant.ProductVariantAlreadyExistsException;
import com.japes.productservice.exception.productvariant.ProductVariantNotFoundException;
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
	
	@Test
	void createProductVariant_ShouldCreateSuccessfully() {
	    // Arrange
	    when(productRepository.findById(createRequest.getProductId())).thenReturn(Optional.of(product));
	    when(productVariantRepository.existsBySkuCode(createRequest.getSkuCode())).thenReturn(false);
	    when(productVariantRepository.save(any(ProductVariant.class))).thenReturn(productVariant);

	    // Act
	    ProductVariantResponse result = productVariantServiceImpl.createProductVariant(createRequest);

	    // Assert
	    assertNotNull(result);

	    assertEquals(productVariant.getId(), result.getId());
	    assertEquals(product.getId(), result.getProductId());
	    assertEquals(product.getName(), result.getProductName());

	    assertEquals(productVariant.getSkuCode(), result.getSkuCode());
	    assertEquals(productVariant.getPrice(), result.getPrice());
	    assertEquals(productVariant.getActive(), result.getActive());

	    assertEquals(1, result.getAttributes().size());

	    VariantAttributeResponse attribute = result.getAttributes().get(0);

	    assertEquals(variantAttribute.getAttributeName(), attribute.getAttributeName());
	    assertEquals(variantAttribute.getAttributeValue(), attribute.getAttributeValue());

	    // Verify
	    verify(productRepository).findById(createRequest.getProductId());
	    verify(productVariantRepository).existsBySkuCode(createRequest.getSkuCode());
	    verify(productVariantRepository).save(any(ProductVariant.class));
	}
	
	@Test
	void createProductVariant_ShouldThrowProductNotFoundException() {
	    // Arrange
	    when(productRepository.findById(createRequest.getProductId())).thenReturn(Optional.empty());

	    // Act & Assert
	    ProductNotFoundException exception =
	            assertThrows(ProductNotFoundException.class,
	                    () -> productVariantServiceImpl.createProductVariant(createRequest));

	    assertEquals("Product with ID " + createRequest.getProductId() +" not found", exception.getMessage());

	    // Verify
	    verify(productRepository).findById(createRequest.getProductId());
	    verify(productVariantRepository, never()).existsBySkuCode(anyString());
	    verify(productVariantRepository, never()).save(any());
	}
	
	@Test
	void createProductVariant_ShouldThrowProductVariantAlreadyExistsException() {
	    // Arrange
	    when(productRepository.findById(createRequest.getProductId())).thenReturn(Optional.of(product));

	    when(productVariantRepository.existsBySkuCode(createRequest.getSkuCode())).thenReturn(true);

	    // Act & Assert
	    ProductVariantAlreadyExistsException exception =
	            assertThrows(ProductVariantAlreadyExistsException.class,
	                    () -> productVariantServiceImpl.createProductVariant(createRequest));

	    assertEquals("Product variant with SKU "+ createRequest.getSkuCode() +" already exists", exception.getMessage());

	    // Verify
	    verify(productRepository).findById(createRequest.getProductId());
	    verify(productVariantRepository).existsBySkuCode(createRequest.getSkuCode());
	    verify(productVariantRepository, never()).save(any());
	}
	
	@Test
	void getProductVariantById_ShouldReturnVariantSuccessfully() {
	    // Arrange
	    when(productVariantRepository.findById(productVariant.getId())).thenReturn(Optional.of(productVariant));

	    // Act
	    ProductVariantResponse result = productVariantServiceImpl.getProductVariantById(productVariant.getId());

	    // Assert
	    assertNotNull(result);

	    assertEquals(productVariant.getId(), result.getId());
	    assertEquals(product.getId(), result.getProductId());
	    assertEquals(product.getName(), result.getProductName());

	    assertEquals(productVariant.getSkuCode(), result.getSkuCode());
	    assertEquals(productVariant.getPrice(), result.getPrice());
	    assertEquals(productVariant.getActive(), result.getActive());

	    assertEquals(1, result.getAttributes().size());

	    VariantAttributeResponse attribute = result.getAttributes().get(0);

	    assertEquals(variantAttribute.getAttributeName(), attribute.getAttributeName());
	    assertEquals(variantAttribute.getAttributeValue(), attribute.getAttributeValue());

	    // Verify
	    verify(productVariantRepository).findById(productVariant.getId());
	}
	
	@Test
	void getProductVariantById_ShouldThrowProductVariantNotFoundException() {
	    // Arrange
	    when(productVariantRepository.findById(productVariant.getId())).thenReturn(Optional.empty());

	    // Act & Assert
	    ProductVariantNotFoundException exception =
	            assertThrows(ProductVariantNotFoundException.class,
	                    () -> productVariantServiceImpl.getProductVariantById(productVariant.getId()));

	    assertEquals("Product Variant with ID "+ productVariant.getId() +" not found", exception.getMessage());

	    // Verify
	    verify(productVariantRepository).findById(productVariant.getId());
	}
	
	@Test
	void getProductVariantBySkuCode_ShouldReturnVariantSuccessfully() {
	    // Arrange
	    when(productVariantRepository.findBySkuCode(productVariant.getSkuCode())).thenReturn(Optional.of(productVariant));

	    // Act
	    ProductVariantResponse result = productVariantServiceImpl.getProductVariantBySkuCode(productVariant.getSkuCode());

	    // Assert
	    assertNotNull(result);

	    assertEquals(productVariant.getId(), result.getId());
	    assertEquals(product.getId(), result.getProductId());
	    assertEquals(product.getName(), result.getProductName());

	    assertEquals(productVariant.getSkuCode(), result.getSkuCode());
	    assertEquals(productVariant.getPrice(), result.getPrice());
	    assertEquals(productVariant.getActive(), result.getActive());

	    assertEquals(1, result.getAttributes().size());

	    VariantAttributeResponse attribute = result.getAttributes().get(0);

	    assertEquals(variantAttribute.getAttributeName(), attribute.getAttributeName());
	    assertEquals(variantAttribute.getAttributeValue(), attribute.getAttributeValue());

	    // Verify
	    verify(productVariantRepository).findBySkuCode(productVariant.getSkuCode());
	}
	
	@Test
	void getProductVariantBySkuCode_ShouldThrowProductVariantNotFoundException() {
	    // Arrange
	    when(productVariantRepository.findBySkuCode(productVariant.getSkuCode())).thenReturn(Optional.empty());

	    // Act & Assert
	    ProductVariantNotFoundException exception =
	            assertThrows(ProductVariantNotFoundException.class,
	                    () -> productVariantServiceImpl.getProductVariantBySkuCode(productVariant.getSkuCode()));

	    assertEquals("Product Variant with SKU "+ productVariant.getSkuCode() +" not found",  exception.getMessage());

	    // Verify
	    verify(productVariantRepository).findBySkuCode(productVariant.getSkuCode());
	}
	
	@Test
	void getProductVariantsByProductId_ShouldReturnVariantsSuccessfully() {
	    // Arrange
	    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

	    when(productVariantRepository.findByProductId(product.getId())).thenReturn(List.of(productVariant));

	    // Act
	    List<ProductVariantResponse> result = productVariantServiceImpl.getProductVariantsByProductId(product.getId());

	    // Assert
	    assertNotNull(result);

	    assertEquals(1, result.size());

	    ProductVariantResponse response = result.get(0);

	    assertEquals(productVariant.getId(), response.getId());
	    assertEquals(product.getId(), response.getProductId());
	    assertEquals(product.getName(), response.getProductName());

	    assertEquals(productVariant.getSkuCode(), response.getSkuCode());
	    assertEquals(productVariant.getPrice(), response.getPrice());
	    assertEquals(productVariant.getActive(), response.getActive());

	    assertEquals(1, response.getAttributes().size());

	    // Verify
	    verify(productRepository).findById(product.getId());
	    verify(productVariantRepository).findByProductId(product.getId());
	}
	
	@Test
	void getProductVariantsByProductId_ShouldThrowProductNotFoundException() {
	    // Arrange
	    when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

	    // Act & Assert
	    ProductNotFoundException exception =
	            assertThrows(ProductNotFoundException.class,
	                    () -> productVariantServiceImpl.getProductVariantsByProductId(product.getId()));

	    assertEquals("Product with ID "+ product.getId() +" not found", exception.getMessage());

	    // Verify
	    verify(productRepository).findById(product.getId());
	    verify(productVariantRepository, never()).findByProductId(anyLong());
	}
}
