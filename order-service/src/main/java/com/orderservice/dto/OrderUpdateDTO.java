package com.orderservice.dto;

import java.util.List;

public record OrderUpdateDTO(

        List<OrderItemRequestDTO> orderItemRequestDTOList

) {
}
