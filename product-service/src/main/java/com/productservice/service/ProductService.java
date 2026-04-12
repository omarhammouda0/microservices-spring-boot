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

@Slf4j
@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserServiceClient userServiceClient;
    private final HelperService helperService;

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

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {

        log.info("Fetching all active products with pagination - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return productRepository.findAllByIsActiveTrue(pageable)
                .map(productMapper::toResponseDTO);
    }

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
