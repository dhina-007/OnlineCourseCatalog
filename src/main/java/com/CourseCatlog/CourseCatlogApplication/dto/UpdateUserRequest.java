package com.CourseCatlog.CourseCatlogApplication.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    @Email
    private String email;
}