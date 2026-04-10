package com.productervice.mapper;

import com.productervice.dto.ProductCreateDTO;
import com.productervice.dto.ProductResponseDTO;
import com.productervice.dto.ProductWithUserDTO;
import com.productervice.dto.UserResponseDTO;
import com.productervice.entity.Product;
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
