package com.productervice.dto;

import java.time.Instant;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Instant createdAt,
        boolean active
) {}