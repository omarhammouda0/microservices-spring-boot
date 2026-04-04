package com.orderservice.controller;

import com.orderservice.dto.PlaceOrderRequestDTO;
import com.orderservice.dto.PlaceOrderResponseDTO;
import com.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor

@Validated
@RestController
@RequestMapping("/orders")

public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<PlaceOrderResponseDTO> createOrder(@Valid @RequestBody PlaceOrderRequestDTO dto ,
                                                             @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.status ( HttpStatus.CREATED ).body ( orderService.placeOrder ( dto , userId ) );
    }

}
