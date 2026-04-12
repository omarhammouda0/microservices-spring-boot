package com.productservice.dto;

public record InventoryResponseDTO(

        Long productId ,
        Integer quantity ,
        Double price

) {
}
