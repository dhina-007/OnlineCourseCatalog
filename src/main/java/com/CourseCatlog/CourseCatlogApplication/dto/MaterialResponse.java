package com.CourseCatlog.CourseCatlogApplication.dto;

import com.CourseCatlog.CourseCatlogApplication.entity.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MaterialResponse {
    private Long id;
    private MaterialType type;
    private String url;
}