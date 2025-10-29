package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCourseRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    @NotBlank
    private String category;
    @NotBlank
    private String instructor;
    @NotBlank
    private String language;

}