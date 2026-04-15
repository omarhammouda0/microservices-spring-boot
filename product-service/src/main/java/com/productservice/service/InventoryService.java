package com.productservice.service;

import com.productservice.dto.InventoryResponseDTO;
import com.productservice.exception.types.ConflictException;
import com.productservice.exception.types.InventoryNotFoundException;
import com.productservice.mapper.InventoryMapper;
import com.productservice.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for product inventory management.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Expose current stock levels (public read)</li>
 *     <li>Admin-only bulk listing and stock updates</li>
 *     <li>Survive concurrent writers via optimistic locking ({@code @Version})
 *         plus a Spring-Retry loop on {@link OptimisticLockingFailureException}</li>
 * </ul>
 *
 * <p>Inventory is also mutated by RabbitMQ event consumers (OrderCreated /
 * OrderCancelled) — those paths live in {@code OrderEventConsumer}, not here.
 */
@Slf4j
@AllArgsConstructor

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final HelperService  helperService;

    /**
     * Returns the current stock snapshot for a product.
     *
     * @param productId product id
     * @return inventory DTO (productId, quantity, price)
     * @throws InventoryNotFoundException if no inventory row exists for the product
     */
    @Transactional (readOnly = true)
    public InventoryResponseDTO getInventoryByProductId(Long productId ) {

        var inventory = inventoryRepository.findByProductId ( productId ).orElseThrow (
                () -> new InventoryNotFoundException ( productId )
        );

        return inventoryMapper.toResponseDTO ( inventory );

    }

    /**
     * Returns stock snapshots for every product. Admin-only.
     *
     * @param userRole caller role (must be {@code ADMIN})
     * @return list of inventory DTOs
     * @throws com.productservice.exception.types.NotAuthorizedException if caller is not ADMIN
     */
    @Transactional (readOnly = true)
    public List<InventoryResponseDTO> getAllInventory(String userRole) {

        helperService.checkIfAdmin ( userRole );

        return inventoryRepository.findAll ()
                .stream ()
                .map ( inventoryMapper::toResponseDTO )
                .toList ();
    }

    /**
     * Sets the stock quantity for a product. Admin-only.
     *
     * <p>Uses {@code @Version} optimistic locking. On a conflicting concurrent
     * write, Spring Retry automatically re-runs the method up to 3 times with
     * exponential backoff (100ms, 200ms, 400ms). After all retries exhaust,
     * {@link #recoverUpdateInventory} converts the failure into a
     * {@link ConflictException}.
     *
     * @param productId   product to update
     * @param newQuantity new on-hand quantity (must be >= 0, validated upstream)
     * @param userRole    caller role (must be {@code ADMIN})
     * @return the updated inventory DTO
     * @throws com.productservice.exception.types.NotAuthorizedException if caller is not ADMIN
     * @throws InventoryNotFoundException                                 if no inventory exists for the product
     * @throws IllegalStateException                                       if the product is inactive
     */
    @Retryable(
            value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )

    @Transactional
    public InventoryResponseDTO updateInventory(Long productId, Integer newQuantity , String userRole) {

        log.info("Updating inventory for product: {} to quantity: {}", productId, newQuantity);

        helperService.checkIfAdmin ( userRole );


        var inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));


        if (!inventory.getProduct().getIsActive()) {
            log.error("Cannot update inventory for inactive product: {}", productId);
            throw new IllegalStateException("Cannot update inventory for inactive product");
        }

        int oldQuantity = inventory.getQuantity();
        inventory.setQuantity(newQuantity);


            var updated = inventoryRepository.save(inventory);
            log.info("Inventory updated for product {} from {} to {}",
                    productId, oldQuantity, newQuantity);
            return inventoryMapper.toResponseDTO(updated);

    }

    /**
     * Spring Retry recovery handler. Invoked after
     * {@link #updateInventory(Long, Integer, String)} has exhausted all retries
     * on {@link OptimisticLockingFailureException}.
     *
     * @throws ConflictException always (translates persistent concurrency failures
     *                           into a user-visible 409 response)
     */
    @Recover
    public InventoryResponseDTO recoverUpdateInventory(
            OptimisticLockingFailureException e, Long productId, Integer newQuantity, String userRole) {
        log.error("Failed to update inventory for product {} after retries", productId);
        throw new ConflictException ("Inventory update failed due to concurrent modification, please retry");
    }

}
