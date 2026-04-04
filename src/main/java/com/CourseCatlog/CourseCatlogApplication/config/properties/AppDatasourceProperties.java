package com.CourseCatlog.CourseCatlogApplication.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code app.datasource.*} so JDBC settings are not overridden by {@code SPRING_DATASOURCE_URL} / {@code DB_URL}.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.datasource")
public class AppDatasourceProperties {

    private String url;
    private String username;
    private String password = "";
    private String driverClassName = "org.postgresql.Driver";
}
