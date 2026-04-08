package com.orderservice.event;

import com.orderservice.entity.OrderItem;

import java.util.List;

import java.util.UUID;

public record OrderCancelledEvent(
        UUID eventId ,
        Long orderId ,
        List<OrderCancelledItem> items
) {
    public record OrderCancelledItem(
            Long productId ,
            Integer quantity
    ) {
    }
}