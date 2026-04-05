package com.productservice.service;


import com.productservice.client.UserClient;
import com.productservice.dto.UserResponseDTO;
import com.productservice.exception.types.ProductNotFoundException;
import com.productservice.mapper.ProductMapper;
import com.productservice.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ProductService productService;

    @Mock
    private UserClient userClient;

    @Mock
    private RedisTemplate<String, UserResponseDTO> redisTemplate;

    @Mock
    private ValueOperations<String, UserResponseDTO> valueOperations;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Test
    @DisplayName("returnCashedUserFromRedis")
    public void shouldReturnCachedUserWhenRedisHasData() {

//        given

        UserResponseDTO user = new UserResponseDTO (
                1L ,
                "test name" ,
                "test email" ,
                Instant.now ( ) ,
                true
        );

//        when

        when ( redisTemplate.opsForValue ( ) ).thenReturn ( valueOperations );
        when ( valueOperations.get ( "user:1" ) ).thenReturn ( user );

        var result = userServiceClient.getUserByIdFallback ( 1L ,
                new RuntimeException ( "User service down" ) );

        //then

        assertThat ( result ).isNotNull ( );
        assertThat ( result ).isInstanceOf ( UserResponseDTO.class );
        assertThat ( result ).isEqualTo ( user );

    }

    @Test
    @DisplayName("shouldReturnFallbackObjectWhenRedisIsEmpty")
    public void shouldReturnFallBackWhenRedisIsEmpty() {

        //when

        when ( redisTemplate.opsForValue ( ) ).thenReturn ( valueOperations );
        when ( valueOperations.get ( "user:1" ) ).thenReturn ( null );

        var result = userServiceClient.getUserByIdFallback ( 1L ,
                new RuntimeException ( "User service down" ) );

        //then

        assertThat ( result.name ( ) ).isEqualTo ( "Service Unavailable" );
        assertThat ( result.email ( ) ).isEqualTo ( "[unavailable@system.com]" );

    }

    @Test
    @DisplayName("shouldReturnFallbackObjectWhenRedisIsDown")
    public void shouldReturnFallbackObjectWhenRedisIsDown() {

        //when

        when ( redisTemplate.opsForValue ( ) ).thenReturn ( valueOperations );
        when ( valueOperations.get ( "user:1" ) ).thenThrow ( new RuntimeException ( "Redis is down" ) );

        var result = userServiceClient.getUserByIdFallback ( 1L ,
                new RuntimeException ( "User service down" ) );

        //then

        assertThat ( result.name ( ) ).isEqualTo ( "Service Unavailable" );
        assertThat ( result.email ( ) ).isEqualTo ( "[unavailable@system.com]" );

    }

    @Test
    @DisplayName("shouldFetchUserFromUserServiceAndCacheInRedis")
    public void shouldFetchUserFromUserServiceAndCacheInRedis() {

        UserResponseDTO user = new UserResponseDTO (
                1L ,
                "test name" ,
                "test email" ,
                Instant.now ( ) ,
                true
        );

        //when

        when ( userClient.getUserById ( 1L ) ).thenReturn ( user );
        when ( redisTemplate.opsForValue ( ) ).thenReturn ( valueOperations );

        var result = userServiceClient.getUser ( 1L );

        //then

        verify ( valueOperations ).set ( eq ( "user:1" ) , eq ( user ) , any ( Duration.class ) );
        assertThat ( result ).isEqualTo ( user );

    }

    @Test
    @DisplayName("shouldThrowExceptionWhenProductNotFound")
    public void shouldThrowExceptionWhenProductNotFound() {

        when ( productRepository.findById ( 1L ) ).thenReturn ( Optional.empty ( ) );
        ;

        assertThatThrownBy ( () -> productService.getProductById ( 1L ) ).
                isInstanceOf ( ProductNotFoundException.class );
    }

    @Test
    @DisplayName ( "shouldReturnCachedUserFromRedisAndNotCallUserService" )
    public void shouldReturnCachedUserFromRedisAndNotCallUserService() {
        // Arrange
        UserResponseDTO user = new UserResponseDTO (
                1L ,
                "test name" ,
                "test email" ,
                Instant.now ( ) ,
                true
        );
        when ( redisTemplate.opsForValue ( ) ).thenReturn ( valueOperations );
        when ( valueOperations.get ( "user:1" ) ).thenReturn ( user );

        // act

        var result = userServiceClient.getUser ( 1L );

        // assert
        assertThat ( result ).isEqualTo ( user );
        verify ( userClient , never () ) .getUserById ( 1L );

    }

}

