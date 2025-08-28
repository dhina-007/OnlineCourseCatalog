package com.CourseCatlog.CourseCatlogApplication.dto;


import lombok.Data;

@Data
public class UpdateLessonRequest {
    private String title;
    private String content;
    private String duration;
    private String videoUrl;
}