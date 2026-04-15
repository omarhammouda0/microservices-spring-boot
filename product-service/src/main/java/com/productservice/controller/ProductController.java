package com.productservice.controller;

import com.productservice.dto.ProductCreateDTO;
import com.productservice.dto.ProductResponseDTO;
import com.productservice.dto.ProductUpdateDTO;
import com.productservice.dto.ProductWithUserDTO;
import com.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product Management", description = "Endpoints for managing products in the catalog")
@SecurityRequirement(name = "BearerAuth")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Creates a new product in the catalog. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "409", description = "Product with same name already exists")
    })
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductCreateDTO dto,
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole,
            @Parameter(description = "ID of the user creating the product") @RequestHeader("X-User-Id") Long userId) {

        log.info("Creating product by user: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(dto, userRole, userId));
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieves a paginated list of all active products. Accessible by all authenticated users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Pagination parameters") @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Fetching all products with pagination: page {}, size {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves detailed information about a specific product including user details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductWithUserDTO> getProductById(
            @Parameter(description = "Product ID", required = true, example = "1") @Positive @PathVariable Long id) {
        log.info("Fetching product by id: {}", id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get products by user ID",
            description = "Retrieves all products created by a specific user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Page<ProductResponseDTO>> getProductsByUserId(
            @Parameter(description = "User ID", required = true, example = "1") @Positive @PathVariable Long userId,
            @Parameter(description = "Pagination parameters") @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Fetching products for user: {} with pagination: page {}, size {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(productService.getProductsByUserId(userId, pageable));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update product",
            description = "Updates an existing product. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "Product ID", required = true, example = "1") @Positive @PathVariable Long id,
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody ProductUpdateDTO dto) {

        log.info("Updating product: {} by admin", id);
        return ResponseEntity.ok(productService.updateProduct(id, dto, userRole));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete product (soft delete)",
            description = "Soft deletes a product by setting is_active=false. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true, example = "1") @Positive @PathVariable Long id,
            @Parameter(description = "User role (ADMIN required)", required = true) @RequestHeader("X-User-Role") String userRole) {

        log.info("Deleting product: {} by admin", id);
        productService.deleteProduct(id, userRole);
        return ResponseEntity.noContent().build();
    }
}