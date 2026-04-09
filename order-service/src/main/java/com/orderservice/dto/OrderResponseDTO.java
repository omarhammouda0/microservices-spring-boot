package com.orderservice.dto;

import com.orderservice.enums.OrderStatus;
import java.time.Instant;
import java.util.List;

public record OrderResponseDTO(

        Long id ,
        Long userId ,
        List<OrderItemResponseDTO> items ,
        OrderStatus status ,
        Double totalAmount ,
        Instant createdAt ,
        Instant updatedAt

) {
}
