package com.orderservice.dto;

import java.time.Instant;

public record OrderItemResponseDTO(

        Long id,
        Long productId,
        Integer quantity ,
        Double itemPrice ,
        Instant createdAt

) {
}

