package com.CourseCatlog.CourseCatlogApplication.dto;

import lombok.Builder;
import java.time.Instant;

@Builder
public record CourseResponse(
        Long id,
        String title,
        String description,
        String category,
        String instructor,
        String language,
        Instant createdAt
) {}
