package com.productservice.exception.types;

import com.productservice.exception.ErrorCode.ErrorCode;
import com.productservice.exception.base.AppException;
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
