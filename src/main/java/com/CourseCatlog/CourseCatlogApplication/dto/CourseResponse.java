package com.CourseCatlog.CourseCatlogApplication.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
public record CourseResponse(
        Long id,
        String title,
        String description,
        String category,
        String instructorName,
        String language,
        Instant createdAt,
        BigDecimal price,
        String currency,
        boolean free,
        Set<String> tags,
        Double averageRating,
        String videoUrl,
        String imageUrl
) {
}
