package com.CourseCatlog.CourseCatlogApplication.dto;

//import jakarta.validation.constraints.NotBlank;
//import lombok.Builder;
//import lombok.Data;
//
//import java.time.Instant;
//
//@Data @Builder
//public class CourseResponse {
//    private Long id;
//    private String title;
//    private String description;
//    private String category;
//    private String instructor;
//    private String language;
//    private Instant createdAt;
//}


import lombok.Builder;
import java.time.Instant;

@Builder
public record CourseResponse(
        Long id,
        String title,
        String description,
        String category,
        String instructor,
        String language,
        Instant createdAt
) {}