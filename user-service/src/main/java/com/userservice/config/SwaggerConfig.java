package com.userservice.config;

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
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("""
                                User management service for the microservices e-commerce platform.
                                
                                **Features:**
                                - User registration and management
                                - Role-based access control (USER/ADMIN)
                                - User profile management
                                - Soft delete functionality
                                - JWT token generation for authentication
                                - Admin-only user management operations
                                
                                **Authentication:**
                                - Users can register and login
                                - JWT tokens are required for protected endpoints
                                - Admin users have elevated privileges
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
                                        
                                        Regular users can view and update their own profile.
                                        Admin users can view all users and manage accounts.
                                        """)))
                .tags(List.of(
                        new Tag().name("User Management").description("Create, update, and delete users (Admin only)"),
                        new Tag().name("User Queries").description("Retrieve user information"),
                        new Tag().name("Authentication").description("Login and registration endpoints")
                ));
    }
}