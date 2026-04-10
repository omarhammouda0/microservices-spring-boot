package com.productervice.controller;

import com.productervice.dto.ProductCreateDTO;
import com.productervice.dto.ProductResponseDTO;
import com.productervice.dto.ProductUpdateDTO;
import com.productervice.dto.ProductWithUserDTO;
import com.productervice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor

@RestController
@RequestMapping("/products")

@Validated

public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct
            (@Valid @RequestBody ProductCreateDTO dto ,
             @RequestHeader("X-User-Role") String userRole ,
             @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.status ( HttpStatus.CREATED ).body ( productService.createProduct
                ( dto , userRole , userId ) );
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok ( productService.getAllProducts ( ) );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductWithUserDTO> getProductById(@Positive @PathVariable Long id) {
        return ResponseEntity.ok ( productService.getProductById ( id ) );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByUserId
            (@Positive @PathVariable Long userId) {
        return ResponseEntity.ok ( productService.getProductsByUserId ( userId ) );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@Positive @PathVariable Long id ,
                                                            @RequestHeader("X-User-Role") String userRole ,
                                                            @Valid @RequestBody ProductUpdateDTO dto) {
        return ResponseEntity.ok ( productService.updateProduct ( id , dto , userRole ) );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@Positive @PathVariable Long id ,
                                              @RequestHeader("X-User-Role") String userRole) {

        productService.deleteProduct ( id , userRole );
        return ResponseEntity.noContent ( ).build ( );
    }

}
