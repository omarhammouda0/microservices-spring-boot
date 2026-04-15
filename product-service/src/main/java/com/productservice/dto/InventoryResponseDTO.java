package com.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current stock snapshot for a product.")
public record InventoryResponseDTO(

        @Schema(description = "Product identifier.", example = "100")
        Long productId,

        @Schema(description = "Current on-hand quantity.", example = "42")
        Integer quantity,

        @Schema(description = "Unit price in USD.", example = "29.99")
        Double price

) {
}
