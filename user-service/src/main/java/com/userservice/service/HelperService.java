package com.userservice.service;


import com.userservice.enums.Role;
import com.userservice.exception.types.NotAuthorizedException;
import org.springframework.stereotype.Component;

/**
 * Centralized RBAC / authorization helper used across service methods.
 *
 * <p>All checks compare values that originated from the signed JWT (derived
 * by the API Gateway and forwarded as {@code X-User-Id} / {@code X-User-Role}
 * headers). Direct client access to this service is blocked at the Docker
 * network boundary, so these header values can be trusted.
 */
@Component
public class HelperService {

    /**
     * Asserts the caller holds the {@code ADMIN} role.
     *
     * @param userRole role string extracted from the request headers
     * @throws NotAuthorizedException if the role is anything other than {@code ADMIN}
     */
    public void checkIfAdmin (String userRole) {

        if (!userRole.equals( Role.ADMIN.toString () )) {
            throw new NotAuthorizedException (  );
        }

    }

    /**
     * Asserts the caller's id matches the id of the resource owner.
     *
     * @param requiredUserId id of the resource owner
     * @param currentUserId  id of the caller
     * @throws NotAuthorizedException if the ids differ
     */
    public void checkUserIdentity (Long requiredUserId , Long currentUserId) {

        if (!currentUserId.equals(requiredUserId)) {
            throw new NotAuthorizedException (  );
        }

    }

    /**
     * Asserts the caller holds exactly the given role.
     *
     * @param requiredUserRole the expected role
     * @param currentUserRole  the caller's role
     * @throws NotAuthorizedException if the roles differ
     */
    public void checkUserRole (String requiredUserRole , String currentUserRole) {

        if (! requiredUserRole.equals (currentUserRole)) {
            throw new NotAuthorizedException (  );
        }

    }


}



