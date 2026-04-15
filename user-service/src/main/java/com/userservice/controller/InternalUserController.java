package com.userservice.controller;

import com.userservice.dto.UserResponseDTO;
import com.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal service-to-service endpoint consumed by product-service via Feign.
 * <p>
 * This controller is intentionally hidden from the public Swagger UI with
 * {@link Hidden}. It is only reachable inside the Docker network; the API
 * Gateway does not route {@code /internal/**} paths from the public internet.
 */
@Hidden
@RestController
@RequestMapping("/internal/users")
@AllArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getInternalUserById(id));
    }
}