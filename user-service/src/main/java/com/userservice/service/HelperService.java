package com.userservice.service;


import com.userservice.enums.Role;
import com.userservice.exception.types.NotAuthorizedException;
import org.springframework.stereotype.Component;

@Component
public class HelperService {



    public void checkIfAdmin (String userRole) {

        if (!userRole.equals( Role.ADMIN.toString () )) {
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



