package com.orderservice.service;

import com.orderservice.client.ProductClient;
import com.orderservice.dto.InventoryResponseDTO;
import com.orderservice.exception.types.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Slf4j
@Service

public class ProductServiceClient {

    private final ProductClient productClient;

    @CircuitBreaker(name = "productservice")
    @Retry(name = "productservice", fallbackMethod = "getInventoryByIdFallback")

    public InventoryResponseDTO getInventoryByProductId(Long productId) {
        return productClient.getInventory ( productId );
    }

    public InventoryResponseDTO getInventoryByIdFallback(Long productId , Throwable ex) {
        if (ex instanceof CallNotPermittedException) {
            log.warn ( "Circuit is OPEN for product {}" , productId );
        } else {
            log.warn ( "Product Service unavailable for product {}. Reason: {}" ,
                    productId , ex.getClass ( ).getSimpleName ( ) );
        }
        throw new ServiceUnavailableException (
                "Product service is currently unavailable, please try again later"
        );
    }

}
