package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.config.properties.SupabaseStorageProperties;
import com.CourseCatlog.CourseCatlogApplication.constant.MediaConstants;
import com.CourseCatlog.CourseCatlogApplication.exception.BadRequestException;
import com.CourseCatlog.CourseCatlogApplication.exception.TechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class CourseVideoStorageService {

    private final SupabaseStorageProperties properties;
    private final S3Client s3Client;

    public CourseVideoStorageService(
            SupabaseStorageProperties properties,
            @Autowired(required = false) S3Client s3Client) {
        this.properties = properties;
        this.s3Client = s3Client;
    }

    /**
     * Uploads a course cover image and returns the public URL (same bucket / URL prefix as video).
     */
    public String uploadCourseCoverImage(Long courseId, MultipartFile file) {
        if (!properties.isEnabled() || s3Client == null) {
            throw new BadRequestException(
                    "Course storage is not configured (set app.supabase-storage.enabled and env credentials)");
        }
        if (!properties.isCompleteForClient()) {
            throw new BadRequestException("Course storage configuration is incomplete");
        }
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)
                || !contentType.toLowerCase().startsWith(MediaConstants.IMAGE_CONTENT_TYPE_PREFIX)) {
            throw new BadRequestException("Only image/* content types are allowed for cover");
        }
        String safeName = sanitizeImageFilename(file.getOriginalFilename());
        String key = "courses/" + courseId + "/covers/" + UUID.randomUUID() + "-" + safeName;
        long size = file.getSize();
        if (size <= 0) {
            throw new BadRequestException("Image file size is invalid");
        }
        String bucket = properties.getBucket().trim();
        log.debug("S3 PutObject cover bucket={} key={} contentType={} size={}", bucket, key, contentType, size);
        try (InputStream in = file.getInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(in, size));
        } catch (IOException e) {
            throw new TechnicalException("Failed to read uploaded image", e);
        } catch (NoSuchBucketException e) {
            log.warn("S3 bucket not found: bucket={} key={}", bucket, key);
            throw new BadRequestException(
                    "Storage bucket '" + bucket + "' does not exist. In Supabase: Storage → New bucket → use this "
                            + "exact name, or set SUPABASE_S3_BUCKET (and public-url-prefix) to an existing bucket.");
        } catch (S3Exception e) {
            log.error(
                    "S3 PutObject cover failed status={} errorCode={} requestId={} message={} bucket={} key={}",
                    e.statusCode(),
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : null,
                    e.requestId(),
                    e.getMessage(),
                    bucket,
                    key,
                    e);
            throw new TechnicalException("Cover image upload to storage failed", e);
        } catch (SdkException e) {
            log.error("S3-compatible cover upload failed bucket={} key={}", bucket, key, e);
            throw new TechnicalException("Cover image upload to storage failed", e);
        }
        log.debug("S3 PutObject cover completed key={}", key);
        return buildPublicUrl(key);
    }

    /**
     * Uploads a course promo video and returns the public URL for playback.
     */
    public String uploadCourseVideo(Long courseId, MultipartFile file) {
        if (!properties.isEnabled() || s3Client == null) {
            throw new BadRequestException(
                    "Course storage is not configured (set app.supabase-storage.enabled and env credentials)");
        }
        if (!properties.isCompleteForClient()) {
            throw new BadRequestException("Course storage configuration is incomplete");
        }
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Video file is required");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)
                || !contentType.toLowerCase().startsWith(MediaConstants.VIDEO_CONTENT_TYPE_PREFIX)) {
            throw new BadRequestException("Only video/* content types are allowed");
        }
        String safeName = sanitizeFilename(file.getOriginalFilename());
        String key = "courses/" + courseId + "/" + UUID.randomUUID() + "-" + safeName;
        long size = file.getSize();
        if (size <= 0) {
            throw new BadRequestException("Video file size is invalid");
        }
        String bucket = properties.getBucket().trim();
        log.debug("S3 PutObject bucket={} key={} contentType={} size={}", bucket, key, contentType, size);
        try (InputStream in = file.getInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(in, size));
        } catch (IOException e) {
            throw new TechnicalException("Failed to read uploaded video", e);
        } catch (NoSuchBucketException e) {
            log.warn("S3 bucket not found: bucket={} key={}", bucket, key);
            throw new BadRequestException(
                    "Storage bucket '" + bucket + "' does not exist. In Supabase: Storage → New bucket → use this "
                            + "exact name, or set SUPABASE_S3_BUCKET (and public-url-prefix) to an existing bucket.");
        } catch (S3Exception e) {
            log.error(
                    "S3 PutObject failed status={} errorCode={} requestId={} message={} bucket={} key={}",
                    e.statusCode(),
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : null,
                    e.requestId(),
                    e.getMessage(),
                    bucket,
                    key,
                    e);
            throw new TechnicalException("Video upload to storage failed", e);
        } catch (SdkException e) {
            log.error("S3-compatible upload failed bucket={} key={}", bucket, key, e);
            throw new TechnicalException("Video upload to storage failed", e);
        }
        log.debug("S3 PutObject completed key={}", key);
        return buildPublicUrl(key);
    }

    private static String sanitizeFilename(String original) {
        if (!StringUtils.hasText(original)) {
            return "video.bin";
        }
        String name = original;
        int lastSlash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (lastSlash >= 0) {
            name = name.substring(lastSlash + 1);
        }
        String safe = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        return StringUtils.hasText(safe) ? safe : "video.bin";
    }

    private static String sanitizeImageFilename(String original) {
        if (!StringUtils.hasText(original)) {
            return "cover.bin";
        }
        String name = original;
        int lastSlash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (lastSlash >= 0) {
            name = name.substring(lastSlash + 1);
        }
        String safe = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        return StringUtils.hasText(safe) ? safe : "cover.bin";
    }

    private String buildPublicUrl(String key) {
        String prefix = properties.getPublicUrlPrefix().trim();
        while (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix + "/" + key;
    }
}
