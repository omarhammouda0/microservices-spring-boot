package com.userservice.mapper;


import com.userservice.dto.InventoryResponseDTO;
import com.userservice.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {


    public InventoryResponseDTO toResponseDTO(Inventory inventory) {

        return new InventoryResponseDTO (

                inventory.getProductId () ,
                inventory.getQuantity ( ) ,
                inventory.getProduct ().getPrice ()
        );

    }

}
