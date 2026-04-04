package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CreateCourseRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    @NotBlank
    private String category;
    @NotBlank
    private String language;
    private BigDecimal price;
    private String currency;
    private Boolean free;
    @Size(max = 2000)
    private String videoUrl;
    private Set<String> tagNames;
}
