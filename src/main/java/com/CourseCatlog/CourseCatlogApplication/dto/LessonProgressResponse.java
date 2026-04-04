package com.CourseCatlog.CourseCatlogApplication.dto;

import lombok.Builder;

@Builder
public record LessonProgressResponse(
        Long lessonId,
        boolean completed,
        int progressPercent,
        int lastPositionSec
) {
}
