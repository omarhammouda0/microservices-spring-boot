package com.userservice.exception.types;

import com.userservice.exception.ErrorCode.ErrorCode;
import com.userservice.exception.base.AppException;
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