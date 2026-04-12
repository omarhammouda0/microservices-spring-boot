package com.productservice.mapper;

import com.productservice.dto.ProductCreateDTO;
import com.productservice.dto.ProductResponseDTO;
import com.productservice.dto.ProductWithUserDTO;
import com.productservice.dto.UserResponseDTO;
import com.productservice.entity.Product;
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
