package com.apigateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    // StripPrefix runs as a routing filter AFTER this GlobalFilter, so the path
    // at this point still carries the "/api/..." prefix. Match accordingly.
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/",           // login, register, refresh
            "/swagger-ui",          // gateway's own swagger UI
            "/api-docs",            // gateway's own OpenAPI spec
            "/v3/api-docs",         // gateway's own v3 spec
            "/webjars/",            // swagger-ui static assets
            "/actuator/health",     // gateway health check
            "/user-service/",       // proxied swagger routes for user-service
            "/product-service/",    // proxied swagger routes for product-service
            "/order-service/"       // proxied swagger routes for order-service
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange , GatewayFilterChain chain) {

        String path = exchange.getRequest ( ).getURI ( ).getPath ( );

        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);

        if (isPublic) {
            log.debug ( "Public path accessed: {}" , path );
            return chain.filter ( exchange );
        }




        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode( HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }


        String token = authHeader.substring(7);


        if (!jwtService.validateToken(token)) {

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Long userId = jwtService.extractUserId(token);
        String userRole = jwtService.extractUserRole(token);
        log.info("Extracted role: {}", userRole);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers ( h -> {
                    h.remove ( "X-User-Id" );
                    h.remove("X-User-Role");
                } )
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Role", userRole)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}