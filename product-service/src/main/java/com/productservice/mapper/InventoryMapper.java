package com.productservice.mapper;


import com.productservice.dto.InventoryResponseDTO;
import com.productservice.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {


    public InventoryResponseDTO toResponseDTO(Inventory inventory) {

        return new InventoryResponseDTO (

                inventory.getProductId () ,
                inventory.getQuantity ( )
        );

    }

}
