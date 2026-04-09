package com.userservice.dto;

public record InventoryResponseDTO(

        Long productId ,
        Integer quantity ,
        Double price

) {
}
