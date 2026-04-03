package com.productservice.service;

import com.productservice.dto.InventoryResponseDTO;
import com.productservice.exception.types.InventoryNotFoundException;
import com.productservice.exception.types.ProductNotFoundException;
import com.productservice.mapper.InventoryMapper;
import com.productservice.repository.InventoryRepository;
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
