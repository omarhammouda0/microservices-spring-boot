package com.orderservice.exception.types;

import com.orderservice.exception.ErrorCode.ErrorCode;
import com.orderservice.exception.base.AppException;
import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends AppException {

    public ServiceUnavailableException( String msg ) {
        super (

                HttpStatus.SERVICE_UNAVAILABLE ,
                ErrorCode.SERVICE_UNAVAILABLE.toString () ,
                msg

        );
    }

}