package com.CourseCatlog.CourseCatlogApplication.dto;

import com.CourseCatlog.CourseCatlogApplication.entity.MaterialType;
import lombok.Data;

@Data
public class UpdateMaterialRequest {
    private MaterialType type;
    private String url;
}