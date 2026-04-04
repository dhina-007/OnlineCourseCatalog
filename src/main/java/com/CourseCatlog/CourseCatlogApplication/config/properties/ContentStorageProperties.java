package com.CourseCatlog.CourseCatlogApplication.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.content")
public class ContentStorageProperties {

    private String signingSecret = "change-me-content-signing";
    private long signedUrlTtlSeconds = 3600;
}
