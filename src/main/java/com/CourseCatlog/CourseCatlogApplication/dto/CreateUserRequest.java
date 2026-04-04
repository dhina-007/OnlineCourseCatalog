package com.CourseCatlog.CourseCatlogApplication.dto;

import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 128)
    private String password;
    private Role role;
}
