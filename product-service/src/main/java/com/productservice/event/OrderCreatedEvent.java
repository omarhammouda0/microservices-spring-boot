package com.productservice.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(

        UUID eventId ,
        Long orderId ,
        Long productId ,
        Integer quantity ,
        Instant timestamp ,
        String source

) {
}

