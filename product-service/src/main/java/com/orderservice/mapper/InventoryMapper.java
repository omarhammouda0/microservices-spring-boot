package com.orderservice.mapper;


import com.orderservice.dto.InventoryResponseDTO;
import com.orderservice.entity.Inventory;
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
