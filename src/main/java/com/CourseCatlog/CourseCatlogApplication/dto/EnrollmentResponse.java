package com.CourseCatlog.CourseCatlogApplication.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class EnrollmentResponse {
    private Long id;
    private Long userId;
    private Long courseId;
    private Instant enrolledAt;
}
