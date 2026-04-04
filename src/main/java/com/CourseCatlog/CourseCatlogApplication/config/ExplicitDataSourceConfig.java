package com.CourseCatlog.CourseCatlogApplication.config;

import com.CourseCatlog.CourseCatlogApplication.config.properties.AppDatasourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * Registers the primary {@link DataSource} from {@code app.datasource.*}, bypassing Spring Boot's
 * {@code DataSourceAutoConfiguration} binding to {@code SPRING_DATASOURCE_URL} (which often points at localhost in IDE).
 */
@Configuration
@RequiredArgsConstructor
public class ExplicitDataSourceConfig {

    private final AppDatasourceProperties properties;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (!StringUtils.hasText(properties.getUrl())) {
            throw new IllegalStateException("app.datasource.url must be set");
        }
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(properties.getUrl());
        ds.setUsername(properties.getUsername() != null ? properties.getUsername() : "");
        ds.setPassword(properties.getPassword() != null ? properties.getPassword() : "");
        ds.setDriverClassName(properties.getDriverClassName());
        return ds;
    }
}
