package com.productervice.exception.types;


import com.productervice.exception.ErrorCode.ErrorCode;
import com.productervice.exception.base.AppException;
import org.springframework.http.HttpStatus;

public class UserAlreadyDeletedException extends AppException {
    public UserAlreadyDeletedException(Long id) {

        super (
                HttpStatus.BAD_REQUEST ,
                ErrorCode.USER_ALREADY_DELETED.name ( ) ,
                "User with id " + id + " is already deleted"
        );
    }
}
