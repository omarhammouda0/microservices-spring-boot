package com.productservice.config;

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
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .version("1.0.0")
                        .description("""
                                Product and inventory management service for the microservices e-commerce platform.
                                
                                **Features:**
                                - Product CRUD operations (Create, Read, Update, Delete)
                                - Inventory management with stock tracking
                                - Real-time stock validation for orders
                                - Event-driven inventory updates via RabbitMQ
                                - Soft delete for products (is_active flag)
                                - Optimistic locking for inventory updates
                                - Admin-only product management operations
                                
                                **Event Flow:**
                                1. Order Service requests stock validation
                                2. Product Service checks availability
                                3. Order Service places order
                                4. Product Service reserves inventory
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
                                .url("http://localhost:8080/api")
                                .description("API Gateway (all services route through here)")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        Enter JWT token in format: `Bearer <your-token>`
                                        
                                        Admin users can create/update/delete products.
                                        Regular users can only view products.
                                        """)))
                .tags(List.of(
                        new Tag().name("Product Management").description("Create, update, and delete products (Admin only)"),
                        new Tag().name("Product Queries").description("Retrieve product information (All users)"),
                        new Tag().name("Inventory Management").description("Manage and check product inventory (Admin only)")
                ));
    }
}