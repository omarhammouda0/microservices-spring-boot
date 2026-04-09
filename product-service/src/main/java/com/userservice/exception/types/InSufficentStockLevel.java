package com.userservice.exception.types;

import com.userservice.exception.ErrorCode.ErrorCode;
import com.userservice.exception.base.AppException;
import org.springframework.http.HttpStatus;

public class InSufficentStockLevel extends AppException {

    public InSufficentStockLevel(Long id) {
        super (

                HttpStatus.INSUFFICIENT_STORAGE ,
                ErrorCode.INSUFFICIENT_INVENTORY_LEVEL.name ( ) ,
                "Inventory for product with id " + id + " is not enough"

        );
    }

}