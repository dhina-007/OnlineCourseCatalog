package com.CourseCatlog.CourseCatlogApplication.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.stripe")
public class StripeProperties {

    private String apiKey = "";
    private String webhookSecret = "";
}
