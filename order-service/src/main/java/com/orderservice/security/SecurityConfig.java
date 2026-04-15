package com.orderservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CORS is handled exclusively by the API gateway. Services are not browser-reachable
        // under the gateway-only access model (see docker-compose expose:), so a service-level
        // CORS filter would be dead code.
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf( AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll ()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy( SessionCreationPolicy.STATELESS)
                )

                .build();
    }
}
