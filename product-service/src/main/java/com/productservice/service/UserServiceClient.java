package com.productservice.service;

import com.productservice.client.UserClient;
import com.productservice.dto.UserResponseDTO;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Slf4j
@Service

public class UserServiceClient {

    private final UserClient userClient;
    private Map<Long , UserResponseDTO> userCache = new ConcurrentHashMap<> ();

    @CircuitBreaker(name = "userservice")
    @Retry(name = "userservice", fallbackMethod = "getUserByIdFallback")

    public UserResponseDTO getUser (Long userId) {

        log.info("ATTEMPT: Fetching user with id {}", userId);

        if (userId == 999) {
            throw new RuntimeException("Test retry");
        }

        UserResponseDTO user = userClient.getUserById(userId);
        userCache.put ( userId, user );
        log.info ( "Caching user id {}" , userId );

        return user ;
    }

    private UserResponseDTO getUserByIdFallback(Long userId , Throwable  ex) {

        if (ex instanceof CallNotPermittedException) {
            log.warn("Circuit is OPEN for user {}. Trying cache...", userId);
        } else {
            log.warn("User Service unavailable for user {}. Using fallback. Reason: {}",
                    userId, ex.getClass().getSimpleName());
        }

       if ( userCache.containsKey (userId) ) {

           log.info ( "Returning cached data for the user  {}" , userId );
           return userCache.get ( userId );
       }

       log.warn (  "No cached data for the user  {}" , userId );

        return new UserResponseDTO (
                userId ,
                "Service Unavailable" ,
                "[unavailable@system.com]" ,
                Instant.now ( ) ,
                false
        );

    }

}
