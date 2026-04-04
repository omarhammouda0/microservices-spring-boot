package com.orderservice.mapper;

import com.orderservice.dto.ProductCreateDTO;
import com.orderservice.dto.ProductResponseDTO;
import com.orderservice.dto.ProductWithUserDTO;
import com.orderservice.dto.UserResponseDTO;
import com.orderservice.entity.Product;
import org.springframework.stereotype.Component;

@Component

public class ProductMapper {

    public Product toEntity(ProductCreateDTO dto) {

        return Product.builder()

                .name(dto.name ())
                .price(dto.price ())
                .userId(dto.userId ())

                .build();
    }

    public ProductResponseDTO toResponseDTO (Product product) {

        return new ProductResponseDTO(

                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getUserId(),
                product.getCreatedDate()
        );

    }

    public ProductWithUserDTO toProductWithUserDTO (Product product , UserResponseDTO user) {

        return new ProductWithUserDTO (

                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getUserId(),
                product.getCreatedDate (),
                user.name (),
                user.email ()

        );

    }

}
