package com.orderservice.service;

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

}
