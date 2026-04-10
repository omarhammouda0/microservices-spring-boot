package com.productervice.controller;

import com.productervice.dto.InventoryResponseDTO;
import com.productervice.dto.InventoryUpdateDTO;
import com.productervice.service.InventoryService;
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

public class InventoryController {

    private InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> getInventoryByProductId(@Positive @PathVariable Long productId) {
        return ResponseEntity.ok (inventoryService.getInventoryByProductId ( productId ) );

    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory(
            @RequestHeader("X-User-Role") String userRole
    ) {
        log.info("Fetching all inventory by user with role: {}", userRole);
        return ResponseEntity.ok ( inventoryService.getAllInventory ( userRole ) );

    }

    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @Positive(message = "Product ID must be positive")
            @PathVariable Long productId,
            @RequestHeader("X-User-Role") String userRole ,
            @Valid @RequestBody InventoryUpdateDTO updateDTO) {

        log.info("Updating inventory for product: {} to quantity: {}", productId, updateDTO.quantity());

        InventoryResponseDTO updatedInventory = inventoryService.updateInventory
                (productId, updateDTO.quantity() , userRole);

        return ResponseEntity.ok(updatedInventory);
    }



}
