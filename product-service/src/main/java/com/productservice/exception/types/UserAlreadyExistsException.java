package com.productservice.exception.types;



import com.productservice.exception.ErrorCode.ErrorCode;
import com.productservice.exception.base.AppException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends AppException {

    public UserAlreadyExistsException
            (String email) {

        super (

                HttpStatus.CONFLICT ,
                ErrorCode.USER_ALREADY_EXISTS.name ( ) ,
                "User with email " + email + " already exists"

        );


    }

}
