package com.userservice.exception.types;

import com.userservice.exception.ErrorCode.ErrorCode;
import com.userservice.exception.base.AppException;
import org.springframework.http.HttpStatus;


public class NotAuthorizedException extends AppException {
    public NotAuthorizedException() {

        super (
                HttpStatus.FORBIDDEN ,
                ErrorCode.FORBIDDEN_OPERATION.name ( ) ,
                "You are not allowed to perform this action ");
    }
}