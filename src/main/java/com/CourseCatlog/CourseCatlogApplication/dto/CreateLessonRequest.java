package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateLessonRequest {
    @NotBlank(message = "Lesson title is required")
    private String title;
    private String content;
    private String duration;
    private String videoUrl;
}