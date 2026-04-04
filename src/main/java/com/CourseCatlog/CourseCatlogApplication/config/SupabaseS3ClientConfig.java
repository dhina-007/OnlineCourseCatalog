package com.CourseCatlog.CourseCatlogApplication.config;

import com.CourseCatlog.CourseCatlogApplication.config.properties.SupabaseStorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.LegacyMd5Plugin;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class SupabaseS3ClientConfig {

    @Bean
    @ConditionalOnProperty(prefix = "app.supabase-storage", name = "enabled", havingValue = "true")
    public S3Client supabaseS3Client(SupabaseStorageProperties properties) {
        if (!properties.isCompleteForClient()) {
            throw new IllegalStateException(
                    "app.supabase-storage.enabled=true but endpoint, credentials, bucket, region, "
                            + "and public-url-prefix must all be non-blank");
        }
        // Supabase and other S3-compatible APIs often reject SDK default CRC32 checksum headers (2.30+).
        // LegacyMd5Plugin + WHEN_REQUIRED matches common third-party S3 expectations.
        return S3Client.builder()
                .addPlugin(LegacyMd5Plugin.create())
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .endpointOverride(URI.create(properties.getEndpoint().trim()))
                .region(Region.of(properties.getRegion().trim()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                properties.getAccessKeyId().trim(),
                                properties.getSecretAccessKey().trim())))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
