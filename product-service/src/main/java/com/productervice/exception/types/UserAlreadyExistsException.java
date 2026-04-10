package com.productervice.exception.types;



import com.productervice.exception.ErrorCode.ErrorCode;
import com.productervice.exception.base.AppException;
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
