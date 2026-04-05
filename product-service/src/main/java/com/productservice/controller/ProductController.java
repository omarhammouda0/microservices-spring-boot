package com.productservice.controller;

import com.productservice.client.UserClient;
import com.productservice.dto.ProductCreateDTO;
import com.productservice.dto.ProductResponseDTO;
import com.productservice.dto.ProductUpdateDTO;
import com.productservice.dto.ProductWithUserDTO;
import com.productservice.service.ProductService;
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

    private final UserClient userClient;
    private final ProductService productService;


    @GetMapping ("/health")
    public String hello() {
        return "Hello from Product Service!";
    }

    @GetMapping("/test")
    public String test() {
        String userResponse = userClient.hello ( );
        return "Product Service says: I called User Service and got: " + userResponse;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateDTO dto) {
        return ResponseEntity.status ( HttpStatus.CREATED ).body ( productService.createProduct ( dto ) );
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok ( productService.getAllProducts ( ) );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductWithUserDTO> getProductById(@Positive @PathVariable Long id) {
        return ResponseEntity.ok ( productService.getProductById ( id ) );

    }

    @GetMapping ("/user/{userId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByUserId(@Positive @PathVariable Long userId) {
        return ResponseEntity.ok ( productService.getProductsByUserId ( userId ) );
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@Positive @PathVariable Long id ,
                                                            @Valid @RequestBody ProductUpdateDTO dto) {
        return ResponseEntity.ok ( productService.updateProduct ( id , dto ) );
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteProduct(@Positive @PathVariable Long id) {
        productService.deleteProduct ( id );
        return ResponseEntity.noContent ( ).build ( );
    }

}
