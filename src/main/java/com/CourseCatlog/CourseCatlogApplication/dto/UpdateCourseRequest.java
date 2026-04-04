package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class UpdateCourseRequest {
    private String title;
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    private String category;
    private String language;
    private BigDecimal price;
    private String currency;
    private Boolean free;
    @Size(max = 2000)
    private String videoUrl;
    @Size(max = 2000)
    private String imageUrl;
    private Set<String> tagNames;
}
