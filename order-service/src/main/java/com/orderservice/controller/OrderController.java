package com.orderservice.controller;

import com.orderservice.dto.OrderResponseDTO;
import com.orderservice.dto.PlaceOrderRequestDTO;
import com.orderservice.dto.PlaceOrderResponseDTO;
import com.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
                                                             @RequestHeader("X-User-Id") Long userId ,
                                                             @RequestHeader("X-User-Role") String userRole)
            {

        return ResponseEntity.status ( HttpStatus.CREATED ).body ( orderService.placeOrder ( dto , userId , userRole ) );
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(orderService.getOrder(orderId , userId));
    }


    @GetMapping

    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("X-User-Role") String userRole)
    {

        return ResponseEntity.ok(orderService.getOrders(pageable , userRole));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByUser(

            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("X-User-Id") Long requestingUserId) {


        return ResponseEntity.ok(orderService.getOrdersByUser(requestingUserId, pageable  ));
    }

}
