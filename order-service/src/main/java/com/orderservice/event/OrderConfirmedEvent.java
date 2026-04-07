package com.orderservice.event;

import java.util.UUID;

public record OrderConfirmedEvent(

        UUID eventId,
        Long orderId

) {
}
