package com.orderservice.exception.types;

import com.orderservice.exception.ErrorCode.ErrorCode;
import com.orderservice.exception.base.AppException;
import org.springframework.http.HttpStatus;


public class OrderNotFoundException extends AppException {

    public OrderNotFoundException(Long orderId) {
        super (

                HttpStatus.NOT_FOUND ,
                ErrorCode.ORDER_NOT_FOUND.name ( ) ,
                String.format("Order with id %s not found" , orderId)

        );
    }

}