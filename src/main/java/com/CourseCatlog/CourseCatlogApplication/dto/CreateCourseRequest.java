package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCourseRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank
    private String category;
    @NotBlank
    private String instructor;
    @NotBlank
    private String language;
    private String description;
}