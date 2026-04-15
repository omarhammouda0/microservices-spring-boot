package com.productservice.service;


import com.productservice.enums.Role;
import com.productservice.exception.types.NotAuthorizedException;
import org.springframework.stereotype.Component;

/**
 * Centralized RBAC / authorization helper used across product-service methods.
 *
 * <p>All checks operate on the {@code X-User-Id} / {@code X-User-Role} headers
 * forwarded by the API Gateway after JWT validation. Direct network access to
 * product-service is blocked (docker-compose has no public port mapping).
 */
@Component
public class HelperService {

    /**
     * Asserts the caller holds the {@code ADMIN} role.
     *
     * @param userRole role string from the request header
     * @throws NotAuthorizedException if the caller is not ADMIN
     */
    public void checkIfAdmin (String userRole) {

        if (!userRole.equals( Role.ADMIN.toString () )) {
            throw new NotAuthorizedException (  );
        }

    }

    /**
     * Asserts the caller's id matches the target resource owner.
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
     * @param requiredUserRole expected role
     * @param currentUserRole  caller's role
     * @throws NotAuthorizedException if the roles differ
     */
    public void checkUserRole (String requiredUserRole , String currentUserRole) {

        if (! requiredUserRole.equals (currentUserRole)) {
            throw new NotAuthorizedException (  );
        }

    }


}



