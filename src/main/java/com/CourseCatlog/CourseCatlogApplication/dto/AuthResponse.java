package com.CourseCatlog.CourseCatlogApplication.dto;

import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import lombok.Builder;

@Builder
public record AuthResponse(String token, String email, Long userId, Role role) {
}
