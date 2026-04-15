package com.orderservice.controller;

import com.orderservice.dto.OrderResponseDTO;
import com.orderservice.dto.PlaceOrderRequestDTO;
import com.orderservice.dto.PlaceOrderResponseDTO;
import com.orderservice.enums.OrderStatus;
import com.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order Management", description = "Endpoints for managing customer orders")
@SecurityRequirement(name = "BearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Place a new order",
            description = "Creates a new order with stock validation. Requires USER role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order data or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires USER role"),
            @ApiResponse(responseCode = "422", description = "Validation error in order items")
    })
    public ResponseEntity<PlaceOrderResponseDTO> createOrder(
            @Valid @RequestBody PlaceOrderRequestDTO dto,
            @Parameter(description = "User ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "User role (USER required)", required = true) @RequestHeader("X-User-Role") String userRole) {

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(dto, userId, userRole));
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves detailed information about a specific order. Users can only view their own orders; admins can view any order."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot access another user's order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponseDTO> getOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "User ID", required = true) @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(orderService.getOrder(orderId, userId));
    }

    @GetMapping
    @Operation(
            summary = "Get all orders (Admin only)",
            description = "Retrieves a paginated list of all orders. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            @Parameter(description = "Pagination parameters") @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole) {

        return ResponseEntity.ok(orderService.getOrders(pageable, userRole));
    }

    @GetMapping("/my")
    @Operation(
            summary = "Get my orders",
            description = "Retrieves orders for the authenticated user with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByUser(
            @Parameter(description = "Pagination parameters") @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "User ID", required = true) @RequestHeader("X-User-Id") Long requestingUserId,
            @Parameter(description = "User role", required = true) @RequestHeader("X-User-Role") String userRole) {

        return ResponseEntity.ok(orderService.getOrdersByUser(requestingUserId, pageable, requestingUserId, userRole));
    }

    @PutMapping("/{orderId}/status")
    @Operation(
            summary = "Update order status (Admin only)",
            description = "Updates the status of an order. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponseDTO> updateOrderStatusForAdmin(
            @Parameter(description = "Order ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "New order status", required = true) @RequestBody OrderStatus newOrderStatus,
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole) {

        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newOrderStatus, userRole));
    }

    @DeleteMapping("/{orderId}")
    @Operation(
            summary = "Cancel order",
            description = "Cancels an order. Users can cancel their own orders."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponseDTO> deleteOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "User ID", required = true) @RequestHeader("X-User-Id") Long userId) {

        OrderResponseDTO cancelledOrder = orderService.orderCancel(orderId, userId);
        return ResponseEntity.ok(cancelledOrder);
    }
}