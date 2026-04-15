package com.productservice.controller;

import com.productservice.dto.InventoryResponseDTO;
import com.productservice.dto.InventoryUpdateDTO;
import com.productservice.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory Management", description = "Endpoints for managing product inventory")
@SecurityRequirement(name = "BearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    @Operation(
            summary = "Get inventory by product ID",
            description = "Retrieves current inventory level for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Inventory not found for product")
    })
    public ResponseEntity<InventoryResponseDTO> getInventoryByProductId(
            @Parameter(description = "Product ID", required = true, example = "1") @Positive @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @GetMapping
    @Operation(
            summary = "Get all inventory (Admin only)",
            description = "Retrieves all inventory records. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory(
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole) {
        log.info("Fetching all inventory by user with role: {}", userRole);
        return ResponseEntity.ok(inventoryService.getAllInventory(userRole));
    }

    @PutMapping("/{productId}")
    @Operation(
            summary = "Update inventory",
            description = "Updates inventory quantity for a product. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity value"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Product or inventory not found")
    })
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @Parameter(description = "Product ID", required = true, example = "1") @Positive(message = "Product ID must be positive") @PathVariable Long productId,
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody InventoryUpdateDTO updateDTO) {

        log.info("Updating inventory for product: {} to quantity: {}", productId, updateDTO.quantity ());

        InventoryResponseDTO updatedInventory = inventoryService.updateInventory(productId, updateDTO.quantity (), userRole);

        return ResponseEntity.ok(updatedInventory);
    }
}