package com.productservice.exception.types;

import com.productservice.exception.ErrorCode.ErrorCode;
import com.productservice.exception.base.AppException;
import org.springframework.http.HttpStatus;


public class NotAuthorizedException extends AppException {
    public NotAuthorizedException() {

        super (
                HttpStatus.FORBIDDEN ,
                ErrorCode.FORBIDDEN_OPERATION.name ( ) ,
                "You are not allowed to perform this action ");
    }
}