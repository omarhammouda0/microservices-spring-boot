package com.productervice.exception.types;

import com.productervice.exception.ErrorCode.ErrorCode;
import com.productervice.exception.base.AppException;
import org.springframework.http.HttpStatus;


public class NotAuthorizedException extends AppException {
    public NotAuthorizedException() {

        super (
                HttpStatus.FORBIDDEN ,
                ErrorCode.FORBIDDEN_OPERATION.name ( ) ,
                "You are not allowed to perform this action ");
    }
}