package com.orderservice.service;

import com.orderservice.dto.InventoryResponseDTO;
import com.orderservice.exception.types.InventoryNotFoundException;
import com.orderservice.mapper.InventoryMapper;
import com.orderservice.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryResponseDTO getInventoryByProductId(Long productId) {

        var inventory = inventoryRepository.findByProductId ( productId ).orElseThrow (
                () -> new InventoryNotFoundException ( productId )
        );

        return inventoryMapper.toResponseDTO ( inventory );

    }

}
