package com.orderservice.client;


import com.orderservice.dto.InventoryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient (name = "PRODUCT-SERVICE")

public interface ProductClient {


    @GetMapping ("/inventory/{productId}")
    InventoryResponseDTO getInventory(@PathVariable Long productId);


}
