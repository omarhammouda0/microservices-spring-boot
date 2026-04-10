package com.productervice.client;

import com.productervice.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient (name = "USER-SERVICE")

public interface UserClient {


    @GetMapping ("/users/{id}")
    UserResponseDTO getUserById (@PathVariable Long id);

}
