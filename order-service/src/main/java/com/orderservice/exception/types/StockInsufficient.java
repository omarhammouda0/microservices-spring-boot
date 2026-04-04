package com.orderservice.exception.types;

import com.orderservice.exception.ErrorCode.ErrorCode;
import com.orderservice.exception.base.AppException;
import org.springframework.http.HttpStatus;



public class StockInsufficient extends AppException {
    public StockInsufficient(Long productId) {

        super (
                HttpStatus.BAD_REQUEST ,
                ErrorCode.STOCK_INSUFFICIENT.name ( ) ,
                String.format("Product with %s has not enough stocks in the inventory" , productId)
        );
    }
}