package com.productervice.exception.types;

import com.productervice.exception.ErrorCode.ErrorCode;
import com.productervice.exception.base.AppException;
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
