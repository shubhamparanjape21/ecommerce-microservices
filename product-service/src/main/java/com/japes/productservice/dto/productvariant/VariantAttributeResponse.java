package com.japes.productservice.dto.productvariant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Variant Attribute Response")
public class VariantAttributeResponse {
	private Long id;
	private String attributeName;
	private String attributeValue;
}
