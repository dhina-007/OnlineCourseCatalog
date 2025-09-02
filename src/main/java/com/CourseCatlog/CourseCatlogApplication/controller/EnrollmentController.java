package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.EnrollRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.EnrollmentResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UserResponse;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.service.EnrollmentService;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    // POST /enrollments (body: userId, courseId)
    @PostMapping("/enrollments")
    public ResponseEntity<EnrollmentResponse> enroll(@RequestBody EnrollRequest req) {
        EnrollmentResponse created = enrollmentService.enroll(req.getUserId(), req.getCourseId());
        return ResponseEntity.created(URI.create("/enrollments/" + created.getId())).body(created);
    }

    // Alias: POST /courses/{courseId}/enroll (body: userId)
    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<?> enrollViaCourse(@PathVariable Long courseId, @RequestBody EnrollRequest req) {
        EnrollmentResponse created = enrollmentService.enroll(req.getUserId(), courseId);
        return ResponseEntity.created(URI.create("/enrollments/" + created.getId()))
                .body(created);
    }

    @GetMapping("/users/{userId}/enrollments")
    public List<?> listByUser(@PathVariable Long userId) {
        return enrollmentService.listByUser(userId);
    }

    @GetMapping("/courses/{courseId}/students")
    public List<UserResponse> studentsInCourse(@PathVariable Long courseId) {
        List<User> students = enrollmentService.studentsInCourse(courseId);
        return students.stream().map(u -> UserResponse.builder()
                .id(u.getId()).name(u.getName()).email(u.getEmail()).build()).toList();
    }
}