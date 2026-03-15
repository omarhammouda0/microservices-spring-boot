package com.userservice.exception.types;

import com.userservice.exception.ErrorCode.ErrorCode;
import com.userservice.exception.base.AppException;
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
