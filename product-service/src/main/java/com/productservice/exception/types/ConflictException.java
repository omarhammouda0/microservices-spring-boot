package com.productservice.exception.types;

import com.productservice.exception.ErrorCode.ErrorCode;
import com.productservice.exception.base.AppException;
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