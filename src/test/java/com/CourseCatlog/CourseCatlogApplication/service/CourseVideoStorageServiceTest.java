package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.config.properties.SupabaseStorageProperties;
import com.CourseCatlog.CourseCatlogApplication.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseVideoStorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Test
    void uploadCourseVideo_givenStorageDisabled_shouldThrowBadRequest() {
        SupabaseStorageProperties props = new SupabaseStorageProperties();
        props.setEnabled(false);
        var service = new CourseVideoStorageService(props, null);
        var file = new MockMultipartFile("file", "a.mp4", "video/mp4", new byte[] {1});

        assertThatThrownBy(() -> service.uploadCourseVideo(1L, file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not configured");
    }

    @Test
    void uploadCourseVideo_givenNonVideoContentType_shouldThrowBadRequest() {
        SupabaseStorageProperties props = configuredProperties();
        var service = new CourseVideoStorageService(props, s3Client);
        var file = new MockMultipartFile("file", "x.pdf", "application/pdf", new byte[] {1});

        assertThatThrownBy(() -> service.uploadCourseVideo(2L, file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("video");
    }

    @Test
    void uploadCourseVideo_givenValidVideo_shouldPutObjectAndReturnPublicUrl() {
        SupabaseStorageProperties props = configuredProperties();
        var service = new CourseVideoStorageService(props, s3Client);
        var file = new MockMultipartFile("file", "promo.mp4", "video/mp4", new byte[] {9, 9, 9});

        String url = service.uploadCourseVideo(5L, file);

        assertThat(url).startsWith("https://example.test/public/bucket/courses/5/");
        assertThat(url).endsWith("-promo.mp4");

        ArgumentCaptor<PutObjectRequest> reqCap = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(reqCap.capture(), any(RequestBody.class));
        PutObjectRequest req = reqCap.getValue();
        assertThat(req.bucket()).isEqualTo("bucket");
        assertThat(req.key()).matches("courses/5/[0-9a-f-]{36}-promo\\.mp4");
        assertThat(req.contentType()).isEqualTo("video/mp4");
    }

    private static SupabaseStorageProperties configuredProperties() {
        SupabaseStorageProperties p = new SupabaseStorageProperties();
        p.setEnabled(true);
        p.setEndpoint("https://x.storage.supabase.co/storage/v1/s3");
        p.setAccessKeyId("ak");
        p.setSecretAccessKey("sk");
        p.setBucket("bucket");
        p.setRegion("us-east-1");
        p.setPublicUrlPrefix("https://example.test/public/bucket/");
        return p;
    }
}
