package com.orderservice.exception.types;

import com.orderservice.exception.ErrorCode.ErrorCode;
import com.orderservice.exception.base.AppException;
import org.springframework.http.HttpStatus;



public class InventoryNotFoundException extends AppException {

    public InventoryNotFoundException(Long id) {
        super (

                HttpStatus.NOT_FOUND ,
                ErrorCode.INVENTORY_NOT_FOUND.name ( ) ,
                "Inventory for product with id " + id + " not found"

        );
    }

}