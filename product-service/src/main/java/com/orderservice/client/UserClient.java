package com.orderservice.client;

import com.orderservice.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient (name = "USER-SERVICE")

public interface UserClient {

    @GetMapping ("/users/test")
    String hello();

    @GetMapping ("/users/{id}")
    UserResponseDTO getUserById (@PathVariable Long id);

}
