package com.productservice.service;


import com.productservice.dto.ProductWithUserDTO;
import com.productservice.dto.UserResponseDTO;
import com.productservice.entity.Product;
import com.productservice.mapper.ProductMapper;
import com.productservice.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceClientTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserServiceClient userServiceClient;


    @Test
    @DisplayName ( "shouldReturnProductWithUserDetailsWhenProductExists" )
    public void shouldReturnProductWithUserDetailsWhenProductExists(){

        // given

        UserResponseDTO user = new UserResponseDTO (
                1L ,
                "test name" ,
                "test email" ,
                Instant.now ( ) ,
                true
        );

        Product product = new Product (
                1L ,
                "Test product" ,
                19.99 ,
                1L ,
                true ,
                Instant.now ()
        );

        //when

        when ( productRepository.findById ( 1L ) ).thenReturn( Optional.of ( product ) );
        when(userServiceClient.getUser(1L)).thenReturn(user);

        ProductWithUserDTO expectedResponse = new ProductWithUserDTO(
                product.getId () ,
                product.getName () ,
                product.getPrice () ,
                product.getUserId () ,
                product.getCreatedDate () ,
                user.name () ,
                user.email ()
        );

        when(productMapper.toProductWithUserDTO (product, user)).thenReturn(expectedResponse);

        var result = productService.getProductById (  1L );

        //assert

        assertThat(result).isEqualTo(expectedResponse);

    }


}