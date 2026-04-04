package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.EnrollmentResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UserResponse;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import com.CourseCatlog.CourseCatlogApplication.service.EnrollmentService;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @PostMapping("/enrollments")
    public ResponseEntity<EnrollmentResponse> enroll(@RequestBody(required = false) EnrollBody body) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        User user = userService.find(p.getId());
        Long courseId = body != null && body.courseId() != null ? body.courseId() : null;
        if (courseId == null) {
            throw new com.CourseCatlog.CourseCatlogApplication.exception.BadRequestException("courseId is required");
        }
        EnrollmentResponse created = enrollmentService.enroll(courseId, user);
        return ResponseEntity.created(URI.create("/api/v1/enrollments/" + created.getId())).body(created);
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<EnrollmentResponse> enrollViaCourse(@PathVariable Long courseId) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        User user = userService.find(p.getId());
        EnrollmentResponse created = enrollmentService.enroll(courseId, user);
        return ResponseEntity.created(URI.create("/api/v1/enrollments/" + created.getId())).body(created);
    }

    @GetMapping("/users/me/enrollments")
    public List<EnrollmentResponse> myEnrollments() {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        return enrollmentService.listByUser(p.getId(), p);
    }

    @GetMapping("/users/{userId}/enrollments")
    public List<EnrollmentResponse> listByUser(@PathVariable Long userId) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        return enrollmentService.listByUser(userId, p);
    }

    @GetMapping("/courses/{courseId}/students")
    public List<UserResponse> studentsInCourse(@PathVariable Long courseId) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        List<User> students = enrollmentService.studentsInCourse(courseId, p);
        return students.stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .build())
                .toList();
    }

    public record EnrollBody(Long courseId) {
    }
}
