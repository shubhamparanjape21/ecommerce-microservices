package com.japes.productservice.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Category details")
public class CategoryResponse {
	@Schema(example = "1")
	private Long id;
	@Schema(example = "Electronics")
	private String name;
	@Schema(example = "Electronic gadgets and accessories")
	private String description;
}
