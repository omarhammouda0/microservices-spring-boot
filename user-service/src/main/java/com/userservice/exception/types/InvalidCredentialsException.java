package com.userservice.exception.types;

import com.userservice.exception.ErrorCode.ErrorCode;
import com.userservice.exception.base.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException() {
        super (
                HttpStatus.UNAUTHORIZED ,
                ErrorCode.INCORRECT_CREDENTIALS .name ( ) ,
                "Please enter the correct credentials"

        );
    }
}