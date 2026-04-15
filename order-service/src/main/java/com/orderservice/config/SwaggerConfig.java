package com.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .version("1.0.0")
                        .description("""
                                Order management service for the microservices e-commerce platform.
                                
                                **Features:**
                                - Create new orders with stock validation
                                - Track order status (PENDING, CONFIRMED, CANCELLED, etc.)
                                - Cancel pending/confirmed orders
                                - View order history by user
                                - Admin order management
                                - Event-driven communication with Product Service
                                
                                **Order Flow:**
                                1. User places order → Stock validation via Product Service
                                2. Order created in PENDING status
                                3. Product Service reserves inventory
                                4. Order status updated to CONFIRMED
                                5. On cancellation, inventory is restored
                                """)
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@ecommerce.com")
                                .url("https://github.com/ecommerce-platform"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Local Development Server (Direct)"),
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("API Gateway Route (Production)")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        Enter JWT token in format: `Bearer <your-token>`
                                        
                                        Regular users can view and manage their own orders.
                                        Admin users can view all orders and update status.
                                        """)))
                .tags(List.of(
                        new Tag().name("Order Management").description("Create, cancel, and manage orders"),
                        new Tag().name("Order Queries").description("Retrieve order information"),
                        new Tag().name("Admin Operations").description("Admin-only order management")
                ));
    }
}