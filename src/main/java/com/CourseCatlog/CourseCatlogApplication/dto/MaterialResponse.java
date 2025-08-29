package com.CourseCatlog.CourseCatlogApplication.dto;

import com.CourseCatlog.CourseCatlogApplication.entity.MaterialType;

public record MaterialResponse(
        Long id,
        MaterialType type,
        String url
) {}