package com.CourseCatlog.CourseCatlogApplication.dto;

import java.util.List;

public record LessonResponse(
        Long id,
        String title,
        String content,
        String duration,
        String videoUrl,
        List<MaterialResponse> resources
) {}