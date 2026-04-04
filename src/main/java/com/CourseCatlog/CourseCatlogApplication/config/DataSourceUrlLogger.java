package com.CourseCatlog.CourseCatlogApplication.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Logs the resolved JDBC URL at startup (password is not part of {@link java.sql.DatabaseMetaData#getURL()}).
 */
@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class DataSourceUrlLogger implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            log.info("Datasource JDBC URL: {}", url);
        }
    }
}
