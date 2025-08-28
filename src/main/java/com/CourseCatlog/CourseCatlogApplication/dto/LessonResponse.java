package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class LessonResponse {
    private Long id;
    private String title;
    private String content;
    private String duration;
    private String videoUrl;
    private List<MaterialResponse> resources;
    //private List<ResourceDto> resources;

}