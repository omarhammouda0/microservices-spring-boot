package com.userservice.dto;

import java.time.Instant;

public record ProductWithUserDTO(

        Long id ,
        String name ,
        Double price ,
        Long userId ,
        Instant createdDate ,
        String userName ,
        String userEmail



) {
}
