package com.productservice.service;

import com.productservice.client.UserClient;
import com.productservice.dto.UserResponseDTO;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import org.mockito.ArgumentMatchers.*;

import java.time.Duration;
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private UserServiceClient userServiceClient;

    @Mock
    private UserClient userClient;

    @Mock
    private RedisTemplate<String, UserResponseDTO> redisTemplate;

    @Mock
    private ValueOperations<String, UserResponseDTO> valueOperations;

    @Test
    @DisplayName ( "returnCashedUserFromRedis" )
    public void shouldReturnCachedUserWhenRedisHasData() {

//        given

        UserResponseDTO user = new UserResponseDTO (
                1L ,
                "test name" ,
                "test email",
                Instant.now () ,
                true
        );

//        when

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:1")).thenReturn(user);

        var result = userServiceClient.getUserByIdFallback ( 1L ,
                new RuntimeException("User service down") );

        //then

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(UserResponseDTO.class);
        assertThat(result).isEqualTo( user );

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
    public void shouldReturnFallbackObjectWhenRedisIsDown(){

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
    public void shouldFetchUserFromUserServiceAndCacheInRedis(){

        UserResponseDTO user = new UserResponseDTO (
                1L ,
                "test name" ,
                "test email",
                Instant.now () ,
                true
        );

        //when

        when ( userClient.getUserById ( 1L ) ).thenReturn ( user );
        when ( redisTemplate.opsForValue ( ) ).thenReturn ( valueOperations );

        var result = userServiceClient.getUser ( 1L );

        //then

        verify(valueOperations).set(eq("user:1"), eq(user), any( Duration.class));
        assertThat(result).isEqualTo(user);

    }

}

