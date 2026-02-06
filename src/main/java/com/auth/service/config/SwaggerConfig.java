package com.auth.service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration for Authentication Service
 * 
 * Access Swagger UI at: http://localhost:8081/swagger-ui.html
 * Access API Docs at: http://localhost:8081/v3/api-docs
 * 
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

        @Value("${server.port:8081}")
        private String serverPort;

        @Bean
        public OpenAPI authServiceOpenAPI() {
                // Define JWT Security Scheme
                final String securitySchemeName = "bearerAuth";

                return new OpenAPI()
                                .info(new Info()
                                                .title("Auth Service API")
                                                .description("Role-based Authentication and Authorization API")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Development Team")
                                                                .email("dev@auth-service.com")
                                                                .url("https://auth-service.com"))
                                                .license(new License()
                                                                .name("Proprietary")
                                                                .url("https://auth-service.com/license")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:" + serverPort)
                                                                .description("Local Development Server"),
                                                new Server()
                                                                .url("https://api.auth-service.com")
                                                                .description("Production Server")))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName,
                                                                new SecurityScheme()
                                                                                .name(securitySchemeName)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Enter JWT token obtained from login endpoint")));
        }
}
