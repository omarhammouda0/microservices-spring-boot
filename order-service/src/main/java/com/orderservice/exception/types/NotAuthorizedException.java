package com.orderservice.exception.types;

import com.orderservice.exception.ErrorCode.ErrorCode;
import com.orderservice.exception.base.AppException;
import org.springframework.http.HttpStatus;


public class NotAuthorizedException extends AppException {
    public NotAuthorizedException() {

        super (
                HttpStatus.FORBIDDEN ,
                ErrorCode.FORBIDDEN_OPERATION.name ( ) ,
                "You are not allowed to perform this action ");
    }
}