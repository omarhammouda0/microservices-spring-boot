package com.productservice.service;

import com.productservice.dto.*;
import com.productservice.exception.types.ProductNotFoundException;
import com.productservice.exception.types.UserNotActiveException;
import com.productservice.mapper.ProductMapper;
import com.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for product catalog management.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Product CRUD (create, read, update, soft-delete) with pagination</li>
 *     <li>Owner validation against user-service via Feign
 *         ({@link UserServiceClient}), with Redis caching and a circuit-breaker
 *         fallback to keep reads working when user-service is down</li>
 *     <li>RBAC enforcement through {@link HelperService} — only ADMIN can
 *         create/update/delete products</li>
 * </ul>
 *
 * <p>All mutating methods are transactional. Reads are {@code readOnly = true}
 * to help Hibernate skip dirty checking.
 */
@Slf4j
@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserServiceClient userServiceClient;
    private final HelperService helperService;

    /**
     * Creates a new product owned by an existing active user. Admin-only.
     *
     * <p>Flow:
     * <ol>
     *     <li>Look up the owner user via user-service (cached / circuit-broken)</li>
     *     <li>Reject if the caller is not ADMIN</li>
     *     <li>Reject if the owner is not active (unless user-service is degraded
     *         and returned the fallback stub)</li>
     *     <li>Persist the product</li>
     * </ol>
     *
     * @param dto      validated product payload (name, price, ownerId)
     * @param userRole caller role (must be {@code ADMIN})
     * @param userId   caller id (for audit logging)
     * @return the newly persisted product DTO
     * @throws com.productservice.exception.types.NotAuthorizedException if caller is not ADMIN
     * @throws com.productservice.exception.types.UserNotActiveException if the owner is soft-deleted
     */
    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO dto, String userRole, Long userId) {

        log.info("Creating product for user: {}", dto.userId());

        UserResponseDTO user = userServiceClient.getUser(dto.userId());
        helperService.checkIfAdmin(userRole);

        if (!UserServiceClient.FALLBACK_USER_NAME.equals(user.name()) && !user.active()) {
            throw new UserNotActiveException(dto.userId());
        }

        var toSave = productMapper.toEntity(dto);
        var saved = productRepository.save(toSave);

        log.info("Product {} created by user: {}", saved.getId(), userId);

        return productMapper.toResponseDTO(saved);
    }

    /**
     * Fetches a single product along with its owner user details.
     *
     * <p>If user-service is unreachable, the circuit breaker returns a fallback
     * user stub and the product is still returned; otherwise an inactive owner
     * causes a {@link UserNotActiveException}.
     *
     * @param id product id
     * @return combined product + owner DTO
     * @throws ProductNotFoundException if the product does not exist or is soft-deleted
     * @throws UserNotActiveException   if the owner is soft-deleted
     */
    @Transactional(readOnly = true)
    public ProductWithUserDTO getProductById(Long id) {

        log.info("Fetching product with id: {}", id);

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getIsActive()) {
            throw new ProductNotFoundException(id);
        }

        log.info("Fetching user with id: {}", product.getUserId());

        UserResponseDTO user = userServiceClient.getUser(product.getUserId());

        if (!UserServiceClient.FALLBACK_USER_NAME.equals(user.name()) && !user.active()) {
            throw new UserNotActiveException(product.getUserId());
        }

        return productMapper.toProductWithUserDTO(product, user);
    }

    /**
     * Returns a paginated list of all active (non-soft-deleted) products.
     * Publicly available (no auth check).
     *
     * @param pageable pagination/sort parameters
     * @return a page of active product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {

        log.info("Fetching all active products with pagination - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return productRepository.findAllByIsActiveTrue(pageable)
                .map(productMapper::toResponseDTO);
    }

    /**
     * Returns a paginated list of active products owned by the given user.
     *
     * <p>Validates the user exists and is active (with circuit breaker fallback)
     * before querying the repository.
     *
     * @param userId   owner id
     * @param pageable pagination/sort parameters
     * @return a page of product DTOs
     * @throws UserNotActiveException if the user exists but is soft-deleted
     */
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getProductsByUserId(Long userId, Pageable pageable) {

        log.info("Fetching products for user: {} with pagination - Page: {}, Size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        // Validate user exists
        UserResponseDTO user = userServiceClient.getUser(userId);

        if (!UserServiceClient.FALLBACK_USER_NAME.equals(user.name()) && !user.active()) {
            throw new UserNotActiveException(userId);
        }

        return productRepository.findByUserIdAndIsActiveTrue(userId, pageable)
                .map(productMapper::toResponseDTO);
    }

    /**
     * Partially updates a product's name and/or price. Admin-only.
     *
     * @param id       product id
     * @param dto      partial update payload (at least one of name/price required)
     * @param userRole caller role (must be {@code ADMIN})
     * @return the updated product DTO
     * @throws com.productservice.exception.types.NotAuthorizedException if caller is not ADMIN
     * @throws IllegalStateException                                      if both fields are null
     * @throws IllegalArgumentException                                   if price is not strictly positive
     * @throws ProductNotFoundException                                   if the product is missing or soft-deleted
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO dto, String userRole) {

        log.info("Updating product with id: {}", id);

        helperService.checkIfAdmin(userRole);

        if (dto.name() == null && dto.price() == null) {
            log.error("No valid fields provided for update for product id: {}", id);
            throw new IllegalStateException("At least one field (name, price) must be provided for update.");
        }

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getIsActive()) {
            log.error("Cannot update inactive product: {}", id);
            throw new ProductNotFoundException(id);
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            product.setName(dto.name().trim());
        }

        if (dto.price() != null) {
            if (dto.price() <= 0) {
                throw new IllegalArgumentException("Price must be positive");
            }
            product.setPrice(dto.price());
        }

        var updated = productRepository.save(product);

        log.info("Product with id {} updated successfully", id);

        return productMapper.toResponseDTO(updated);
    }

    /**
     * Soft-deletes a product by setting {@code isActive = false}. Admin-only.
     *
     * @param id       product id
     * @param userRole caller role (must be {@code ADMIN})
     * @throws com.productservice.exception.types.NotAuthorizedException if caller is not ADMIN
     * @throws ProductNotFoundException                                   if the product is missing or already inactive
     */
    @Transactional
    public void deleteProduct(Long id, String userRole) {

        log.info("Deleting (soft delete) product with id: {}", id);

        helperService.checkIfAdmin(userRole);

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getIsActive()) {
            log.warn("Product {} is already inactive", id);
            throw new ProductNotFoundException(id);
        }

        product.setIsActive(false);
        productRepository.save(product);

        log.info("Product with id {} deactivated successfully", id);
    }
}
