package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data @Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String instructor;
    private String language;
    private Instant createdAt;
}