package com.orderservice.exception.types;

import com.orderservice.exception.ErrorCode.ErrorCode;
import com.orderservice.exception.base.AppException;
import org.springframework.http.HttpStatus;


public class ProductNotFoundException extends AppException {

    public ProductNotFoundException(Long id) {
        super (

                HttpStatus.NOT_FOUND ,
                ErrorCode.USER_NOT_FOUND.name ( ) ,
                "Product with id " + id + " not found"

        );
    }

}