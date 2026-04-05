package com.productservice.exception.types;



import com.productservice.exception.ErrorCode.ErrorCode;
import com.productservice.exception.base.AppException;
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
