package com.CourseCatlog.CourseCatlogApplication.dto;

import lombok.Data;

@Data
public class UpdateCourseRequest {
    private String title;
    private String description;
    private String category;
    private String instructor;
    private String language;
}