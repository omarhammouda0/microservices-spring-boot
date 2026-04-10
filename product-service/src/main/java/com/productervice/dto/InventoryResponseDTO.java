package com.productervice.dto;

public record InventoryResponseDTO(

        Long productId ,
        Integer quantity ,
        Double price

) {
}
