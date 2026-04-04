package com.orderservice.controller;

import com.orderservice.dto.InventoryResponseDTO;
import com.orderservice.service.InventoryService;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
