package com.orderservice.exception.types;



import com.orderservice.enums.OrderStatus;
import com.orderservice.exception.ErrorCode.ErrorCode;
import com.orderservice.exception.base.AppException;
import org.springframework.http.HttpStatus;

public class InvalidStatusTransition extends AppException {

    public InvalidStatusTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.INVALID_STATUS_TRANSITION.name(),
                String.format("Invalid order status transition from %s to %s", oldStatus, newStatus)

        );
    }
}