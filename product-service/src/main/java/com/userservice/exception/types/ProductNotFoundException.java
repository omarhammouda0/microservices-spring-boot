package com.userservice.exception.types;

import com.userservice.exception.ErrorCode.ErrorCode;
import com.userservice.exception.base.AppException;
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