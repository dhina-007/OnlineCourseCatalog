package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateUserRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateUserRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UserResponse;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.exception.ConflictException;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse create(CreateUserRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail()))
            throw new ConflictException("Email already registered");
        User u = User.builder().name(req.getName()).email(req.getEmail()).build();
        return map(userRepository.save(u));
    }

    public UserResponse get(Long id) {
        return map(find(id));
    }

    public UserResponse update(Long id, UpdateUserRequest req) {
        User u = find(id);
        if (StringUtils.hasText(req.getEmail()) && !u.getEmail().equalsIgnoreCase(req.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(req.getEmail()))
                throw new ConflictException("Email already registered");
            u.setEmail(req.getEmail());
        }
        if (req.getName() != null) u.setName(req.getName());
        return map(userRepository.save(u));
    }

    public User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private UserResponse map(User u) {
        return UserResponse.builder().id(u.getId()).name(u.getName()).email(u.getEmail()).build();
    }
}