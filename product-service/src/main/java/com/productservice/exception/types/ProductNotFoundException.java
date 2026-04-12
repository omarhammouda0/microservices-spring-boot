package com.productservice.exception.types;

import com.productservice.exception.ErrorCode.ErrorCode;
import com.productservice.exception.base.AppException;
import org.springframework.http.HttpStatus;


public class ProductNotFoundException extends AppException {

    public ProductNotFoundException(Long id) {
        super (

                HttpStatus.NOT_FOUND ,
                ErrorCode.PRODUCT_NOT_FOUND.name ( ) ,
                "Product with id " + id + " not found"

        );
    }

}