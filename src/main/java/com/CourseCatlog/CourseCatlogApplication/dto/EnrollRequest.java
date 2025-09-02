package com.CourseCatlog.CourseCatlogApplication.dto;

import lombok.Data;

@Data
public class EnrollRequest {
    private Long userId;
    private Long courseId; // used for POST /enrollments
}