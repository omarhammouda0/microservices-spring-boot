package com.productservice.controller;

import com.productservice.dto.ProductCreateDTO;
import com.productservice.dto.ProductResponseDTO;
import com.productservice.dto.ProductUpdateDTO;
import com.productservice.dto.ProductWithUserDTO;
import com.productservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/products")
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductCreateDTO dto,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Creating product by user: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(dto, userRole, userId));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Fetching all products with pagination: page {}, size {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductWithUserDTO> getProductById(@Positive @PathVariable Long id) {
        log.info("Fetching product by id: {}", id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProductResponseDTO>> getProductsByUserId(
            @Positive @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Fetching products for user: {} with pagination: page {}, size {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(productService.getProductsByUserId(userId, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Positive @PathVariable Long id,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody ProductUpdateDTO dto) {

        log.info("Updating product: {} by admin", id);
        return ResponseEntity.ok(productService.updateProduct(id, dto, userRole));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Positive @PathVariable Long id,
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Deleting product: {} by admin", id);
        productService.deleteProduct(id, userRole);
        return ResponseEntity.noContent().build();
    }
}