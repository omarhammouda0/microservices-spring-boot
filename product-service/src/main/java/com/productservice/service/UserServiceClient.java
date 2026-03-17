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

        UserResponseDTO user = userClient.getUserById(userId);

        redisTemplate.opsForValue().set(
                "user:" + userId,
                user,
                Duration.ofMinutes(10)
        );
        log.info("Cached user {} in Redis", userId);

        return user;
    }

    private UserResponseDTO getUserByIdFallback(Long userId , Throwable  ex) {

        if (ex instanceof CallNotPermittedException) {
            log.warn("Circuit is OPEN for user {}. Trying cache...", userId);
        } else {
            log.warn("User Service unavailable for user {}. Using fallback. Reason: {}",
                    userId, ex.getClass().getSimpleName());
        }

        UserResponseDTO cached = redisTemplate.opsForValue().get("user:" + userId);

        if (cached != null) {
            log.info("Returning Redis cached data for user {}", userId);
            return cached;
        }

        log.warn("No cached data in Redis for user {}", userId);

        return new UserResponseDTO (
                userId ,
                "Service Unavailable" ,
                "[unavailable@system.com]" ,
                Instant.now ( ) ,
                false
        );

    }

}
