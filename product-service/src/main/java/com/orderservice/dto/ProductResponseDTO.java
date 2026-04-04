package com.orderservice.dto;

import java.time.Instant;

public record ProductResponseDTO(


        Long id ,
        String name ,
        Double price ,
        Long userId ,
        Instant createdDate

) {
}
