package com.CourseCatlog.CourseCatlogApplication.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.commerce")
public class CommerceProperties {

    private boolean mockPayments = true;
}
