package com.productervice.exception.types;

import com.productervice.exception.ErrorCode.ErrorCode;
import com.productervice.exception.base.AppException;
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