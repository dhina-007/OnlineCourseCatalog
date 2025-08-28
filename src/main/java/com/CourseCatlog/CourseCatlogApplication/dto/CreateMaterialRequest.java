package com.CourseCatlog.CourseCatlogApplication.dto;

import com.CourseCatlog.CourseCatlogApplication.entity.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMaterialRequest {
    @NotNull(message = "Material type is required")
    private MaterialType type;
    @NotBlank(message = "URL is required")
    private String url;
}