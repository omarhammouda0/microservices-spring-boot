package com.orderservice.exception.ErrorCode;


import com.fasterxml.jackson.annotation.JsonSubTypes;

public enum ErrorCode {


    ORDER_NOT_FOUND,
    STOCK_INSUFFICIENT ,
    INVALID_STATUS_TRANSITION,
    FORBIDDEN_OPERATION


}
