package com.orderservice.dto;

import com.orderservice.enums.OrderStatus;

import java.time.Instant;

public record PlaceOrderResponseDTO(

        Long orderId ,
        OrderStatus status ,
        Instant createdAt ,
        Double totalAmount

) {
}
