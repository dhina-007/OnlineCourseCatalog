package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateUserRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateUserRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UserResponse;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateRoleRequest;
import com.CourseCatlog.CourseCatlogApplication.exception.ConflictException;
import com.CourseCatlog.CourseCatlogApplication.exception.ForbiddenException;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        Role role = req.getRole() != null ? req.getRole() : Role.STUDENT;
        User u = User.builder()
                .name(req.getName())
                .email(req.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .build();
        return map(userRepository.save(u));
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return map(find(id));
    }

    @Transactional
    public UserResponse changeRole(Long userId, UpdateRoleRequest req) {
        User u = find(userId);
        Role newRole = req.getRole();
        if (u.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
            if (userRepository.countByRole(Role.ADMIN) <= 1) {
                throw new ForbiddenException("Cannot change role: at least one admin must remain");
            }
        }
        u.setRole(newRole);
        return map(userRepository.save(u));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        User u = find(id);
        if (StringUtils.hasText(req.getEmail()) && !u.getEmail().equalsIgnoreCase(req.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
                throw new ConflictException("Email already registered");
            }
            u.setEmail(req.getEmail().toLowerCase());
        }
        if (req.getName() != null) {
            u.setName(req.getName());
        }
        return map(userRepository.save(u));
    }

    @Transactional(readOnly = true)
    public User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private UserResponse map(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .build();
    }
}
