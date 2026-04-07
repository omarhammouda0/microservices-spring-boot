package com.userservice.event;

import java.util.UUID;

public record UserUpdatedEvent(

        UUID eventId ,
        Long userId

) {
}
