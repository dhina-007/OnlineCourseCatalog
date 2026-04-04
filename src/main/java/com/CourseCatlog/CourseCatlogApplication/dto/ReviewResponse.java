package com.CourseCatlog.CourseCatlogApplication.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ReviewResponse(
        Long id,
        Long userId,
        String userName,
        Long courseId,
        int rating,
        String comment,
        Instant createdAt
) {
}
