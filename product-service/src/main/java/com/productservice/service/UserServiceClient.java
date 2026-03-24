package com.productservice.service;

import com.productservice.client.UserClient;
import com.productservice.dto.UserResponseDTO;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;


@AllArgsConstructor
@Slf4j
@Service

public class UserServiceClient {

    private final UserClient userClient;
    private final RedisTemplate<String, UserResponseDTO> redisTemplate;

    @CircuitBreaker(name = "userservice")
    @Retry(name = "userservice", fallbackMethod = "getUserByIdFallback")

    public UserResponseDTO getUser (Long userId) {

        log.info("ATTEMPT: Fetching user with id {}", userId);

            var cashedUser = getFromRedis ( userId );

            if ( cashedUser != null ) {
                log.info("Cache hit — returning user {} from Redis", userId);
                return cashedUser;
            }


        UserResponseDTO user = userClient.getUserById( userId );

            try {
                redisTemplate.opsForValue().set(
                        "user:" + userId,
                        user,
                        Duration.ofMinutes(10)
                );
                log.info("Cached user {} in Redis", userId);

            } catch (Exception e) {
                log.warn ("Error in cached user {} in Redis", userId, e);

            }

            return user;

    }

    private UserResponseDTO getFromRedis(Long userId) {

        try {
            return redisTemplate.opsForValue ( ).get ( "user:" + userId );

        } catch (Exception e) {

            log.warn ( "Failed to read user {} from Redis. Reason: {}" , userId , e.getMessage ( ) );
            return null;
        }


    }

    UserResponseDTO getUserByIdFallback(Long userId , Throwable ex) {

        if (ex instanceof CallNotPermittedException) {
            log.warn ( "Circuit is OPEN for user {}. Trying cache..." , userId );
        } else {
            log.warn ( "User Service unavailable for user {}. Using fallback. Reason: {}" ,
                    userId , ex.getClass ( ).getSimpleName ( ) );
        }

        UserResponseDTO cashedUser = getFromRedis ( userId );

        if (cashedUser != null) {
            log.info ( "Returning Redis cached data for user {}" , userId );
            return cashedUser;
        }

        log.warn ( "No cached data in Redis for user {}" , userId );

        return new UserResponseDTO (
                userId ,
                "Service Unavailable" ,
                "[unavailable@system.com]" ,
                Instant.now ( ) ,
                false
        );

    }

}


