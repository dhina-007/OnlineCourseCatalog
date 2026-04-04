package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateUserRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateRoleRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateUserRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UserResponse;
import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import com.CourseCatlog.CourseCatlogApplication.exception.ForbiddenException;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
        UserResponse created = userService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/users/" + created.getId())).body(created);
    }

    @GetMapping("/me")
    public UserResponse me() {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        return userService.get(p.getId());
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        if (!p.getId().equals(id) && p.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Cannot access another user's profile");
        }
        return userService.get(id);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse changeRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest req) {
        return userService.changeRole(id, req);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        if (!p.getId().equals(id) && p.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Cannot update another user's profile");
        }
        return userService.update(id, req);
    }
}
