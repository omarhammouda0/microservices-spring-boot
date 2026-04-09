package com.orderservice.service;

import com.orderservice.enums.OrderStatus;
import com.orderservice.exception.types.InvalidStatusTransition;
import com.orderservice.exception.types.NotAuthorizedException;
import org.springframework.stereotype.Component;

@Component
public class HelperService {



    public void checkIfAdmin (String userRole) {

        if (!userRole.equals("ADMIN")) {
            throw new NotAuthorizedException (  );
        }

    }

    public void checkUserIdentity (Long requiredUserId , Long currentUserId) {

        if (!currentUserId.equals(requiredUserId)) {
            throw new NotAuthorizedException (  );
        }

    }

    public void checkUserRole (String requiredUserRole , String currentUserRole) {

        if (! requiredUserRole.equals (currentUserRole)) {
            throw new NotAuthorizedException (  );
        }

    }

    public void checkAdminStatusTransition(OrderStatus requiredStatus , OrderStatus currentStatus) {

        boolean validTransition =
                (currentStatus == OrderStatus.CONFIRMED && requiredStatus == OrderStatus.DELIVERED) ||
                        (currentStatus == OrderStatus.CONFIRMED && requiredStatus == OrderStatus.FAILED) ||
                        (currentStatus == OrderStatus.DELIVERED && requiredStatus == OrderStatus.RETURNED);

        if (!validTransition) {
            throw new InvalidStatusTransition(currentStatus, requiredStatus);
        }
    }



}
