package com.rh.conges.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation (Swagger)
 *
 * NOTE: This configuration is currently disabled because it requires springdoc-openapi dependency.
 *
 * To enable Swagger/OpenAPI documentation:
 * 1. Add the following dependency to pom.xml:
 *    <dependency>
 *        <groupId>org.springdoc</groupId>
 *        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
 *        <version>2.3.0</version>
 *    </dependency>
 *
 * 2. Uncomment the code below
 * 3. Access Swagger UI at: http://localhost:8080/swagger-ui/index.html
 * 4. API docs JSON at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    // Uncomment the code below after adding springdoc-openapi-starter-webmvc-ui dependency

    /*
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")))
                .info(new Info()
                        .title("Leave Management System API")
                        .description("API for managing employee leave requests and balances")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com")
                                .url("https://yourwebsite.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
    */
}

