package com.CourseCatlog.CourseCatlogApplication.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.supabase-storage")
public class SupabaseStorageProperties {

    /**
     * When false, multipart upload returns 400 with a clear message and no S3 client is created.
     */
    private boolean enabled = false;

    /**
     * Supabase S3 API endpoint, e.g. https://&lt;project-ref&gt;.storage.supabase.co/storage/v1/s3
     */
    private String endpoint = "";

    private String accessKeyId = "";

    private String secretAccessKey = "";

    private String bucket = "";

    private String region = "us-east-1";

    /**
     * Public object URL prefix including bucket path, e.g.
     * https://&lt;project-ref&gt;.supabase.co/storage/v1/object/public/course-videos
     */
    private String publicUrlPrefix = "";

    public boolean isCompleteForClient() {
        return enabled
                && StringUtils.hasText(endpoint)
                && StringUtils.hasText(accessKeyId)
                && StringUtils.hasText(secretAccessKey)
                && StringUtils.hasText(bucket)
                && StringUtils.hasText(region)
                && StringUtils.hasText(publicUrlPrefix);
    }
}
