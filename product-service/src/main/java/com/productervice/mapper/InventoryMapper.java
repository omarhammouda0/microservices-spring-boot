package com.productervice.mapper;


import com.productervice.dto.InventoryResponseDTO;
import com.productervice.entity.Inventory;
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
