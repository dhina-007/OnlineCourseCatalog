package com.CourseCatlog.CourseCatlogApplication.dto;

import com.CourseCatlog.CourseCatlogApplication.entity.MaterialType;
import lombok.Builder;

@Builder
public record MaterialResponse(
        Long id,
        MaterialType type,
        String url
) {}