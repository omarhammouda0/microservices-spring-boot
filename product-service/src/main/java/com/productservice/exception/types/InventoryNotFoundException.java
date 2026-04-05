package com.productservice.exception.types;

import com.productservice.exception.ErrorCode.ErrorCode;
import com.productservice.exception.base.AppException;
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