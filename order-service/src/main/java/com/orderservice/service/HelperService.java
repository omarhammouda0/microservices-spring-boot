package com.orderservice.service;

import com.orderservice.enums.OrderStatus;
import com.orderservice.exception.types.InvalidStatusTransition;
import com.orderservice.exception.types.NotAuthorizedException;
import org.springframework.stereotype.Component;

/**
 * Centralized authorization and state-machine helper for order-service.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>RBAC checks on the {@code X-User-Role} header forwarded by the gateway</li>
 *     <li>Self-access identity checks against the order owner</li>
 *     <li>The {@link OrderStatus} transition whitelist used by admin updates</li>
 * </ul>
 *
 * <p>Direct network access to order-service is blocked (no exposed port in
 * docker-compose), so header values can be trusted as originating from the
 * gateway's JWT validation.
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

        if (!userRole.equals("ADMIN")) {
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

    /**
     * Whitelist check for admin-driven order status transitions.
     *
     * <p>Allowed transitions:
     * <ul>
     *     <li>{@code CONFIRMED → DELIVERED}</li>
     *     <li>{@code CONFIRMED → FAILED}</li>
     *     <li>{@code DELIVERED → RETURNED}</li>
     * </ul>
     *
     * <p>User-driven cancellation ({@code PENDING/CONFIRMED → CANCELLED}) is
     * validated directly in {@link OrderService#orderCancel}, not here.
     *
     * @param requiredStatus requested new status
     * @param currentStatus  current order status
     * @throws InvalidStatusTransition if the transition is not in the whitelist
     */
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
