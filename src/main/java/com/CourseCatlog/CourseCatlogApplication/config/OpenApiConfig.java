package com.CourseCatlog.CourseCatlogApplication.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Course Catalog API")
                        .description("Courses, commerce, progress, reviews — use /api/v1/auth/login then Bearer JWT.")
                        .version("v1.0"))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Call POST /api/v1/auth/login, copy token from response, paste here (no 'Bearer ' prefix).")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
