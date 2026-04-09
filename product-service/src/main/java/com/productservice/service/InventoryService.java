package com.productservice.service;

import com.productservice.dto.InventoryResponseDTO;
import com.productservice.exception.types.InventoryNotFoundException;
import com.productservice.mapper.InventoryMapper;
import com.productservice.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@AllArgsConstructor

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final HelperService  helperService;

    @Transactional (readOnly = true)
    public InventoryResponseDTO getInventoryByProductId(Long productId ) {

        var inventory = inventoryRepository.findByProductId ( productId ).orElseThrow (
                () -> new InventoryNotFoundException ( productId )
        );

        return inventoryMapper.toResponseDTO ( inventory );

    }

    @Transactional (readOnly = true)
    public List<InventoryResponseDTO> getAllInventory(String userRole) {

        helperService.checkIfAdmin ( userRole );

        return inventoryRepository.findAll ()
                .stream ()
                .map ( inventoryMapper::toResponseDTO )
                .toList ();
    }

    @Transactional
    public InventoryResponseDTO updateInventory(Long productId, Integer newQuantity , String userRole) {

        log.info("Updating inventory for product: {} to quantity: {}", productId, newQuantity);

        helperService.checkIfAdmin ( userRole );


        var inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));


        if (!inventory.getProduct().getIsActive()) {
            log.error("Cannot update inventory for inactive product: {}", productId);
            throw new IllegalStateException("Cannot update inventory for inactive product");
        }

        int oldQuantity = inventory.getQuantity();
        inventory.setQuantity(newQuantity);


            var updated = inventoryRepository.save(inventory);
            log.info("Inventory updated for product {} from {} to {}",
                    productId, oldQuantity, newQuantity);
            return inventoryMapper.toResponseDTO(updated);

    }

}
