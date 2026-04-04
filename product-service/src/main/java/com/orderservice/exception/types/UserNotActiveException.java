package com.orderservice.exception.types;

import com.orderservice.exception.ErrorCode.ErrorCode;
import com.orderservice.exception.base.AppException;
import org.springframework.http.HttpStatus;



public class UserNotActiveException extends AppException {
    public UserNotActiveException(Long id) {

        super (
                HttpStatus.BAD_REQUEST ,
                ErrorCode.USER_NOT_ACTIVE.name ( ) ,
                "User with id " + id + " is not more active"
        );
    }
}
