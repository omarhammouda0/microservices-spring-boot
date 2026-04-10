package com.productervice.event;

import java.util.UUID;

public record OrderConfirmedEvent(

        UUID eventId,
        Long orderId

) {
}
