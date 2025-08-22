package com.CourseCatlog.CourseCatlogApplication.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Course Catalog API")
                        .description("APIs for Courses, Lessons, Materials, Users & Enrollments")
                        .version("v1.0"));
    }
}