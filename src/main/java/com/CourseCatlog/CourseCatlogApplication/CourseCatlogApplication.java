package com.CourseCatlog.CourseCatlogApplication;

import com.CourseCatlog.CourseCatlogApplication.config.properties.AppDatasourceProperties;
import com.CourseCatlog.CourseCatlogApplication.config.properties.CommerceProperties;
import com.CourseCatlog.CourseCatlogApplication.config.properties.ContentStorageProperties;
import com.CourseCatlog.CourseCatlogApplication.config.properties.JwtProperties;
import com.CourseCatlog.CourseCatlogApplication.config.properties.StripeProperties;
import com.CourseCatlog.CourseCatlogApplication.config.properties.SupabaseStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties({
        AppDatasourceProperties.class,
        JwtProperties.class,
        StripeProperties.class,
        ContentStorageProperties.class,
        CommerceProperties.class,
        SupabaseStorageProperties.class
})
@EnableAsync
@EnableCaching
public class CourseCatlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseCatlogApplication.class, args);
    }
}
