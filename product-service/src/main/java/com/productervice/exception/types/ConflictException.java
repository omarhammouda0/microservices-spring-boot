package com.productervice.exception.types;

import com.productervice.exception.ErrorCode.ErrorCode;
import com.productervice.exception.base.AppException;
import org.springframework.http.HttpStatus;


public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.CONFLICT.name(),
                message
        );
    }
}