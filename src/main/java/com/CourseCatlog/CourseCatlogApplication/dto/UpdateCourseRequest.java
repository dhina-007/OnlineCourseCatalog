package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCourseRequest {
    private String title;
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    private String category;
    private String instructor;
    private String language;
}