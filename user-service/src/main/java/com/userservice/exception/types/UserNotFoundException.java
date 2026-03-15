package com.userservice.exception.types;


import com.userservice.exception.ErrorCode.ErrorCode;
import com.userservice.exception.base.AppException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AppException {


    public UserNotFoundException(Long id) {
        super (

                HttpStatus.NOT_FOUND ,
                ErrorCode.USER_NOT_FOUND.name ( ) ,
                "User with id " + id + " not found"

        );
    }

    public UserNotFoundException(String email) {
        super (

                HttpStatus.NOT_FOUND ,
                ErrorCode.USER_NOT_FOUND.name ( ) ,
                "User with email " + email + " not found"
        );
    }
}
