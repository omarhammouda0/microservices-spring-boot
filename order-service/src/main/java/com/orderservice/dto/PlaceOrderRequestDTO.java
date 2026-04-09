package com.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PlaceOrderRequestDTO(

        @Valid
        @NotEmpty(message = "Order must contain at least one item")
        List<OrderItemRequestDTO> orderItemRequestDTOList

) {
}
