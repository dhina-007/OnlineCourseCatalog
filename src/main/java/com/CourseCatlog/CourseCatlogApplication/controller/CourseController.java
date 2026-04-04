package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.CourseResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.CreateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import com.CourseCatlog.CourseCatlogApplication.service.CourseService;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CreateCourseRequest req) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        var created = courseService.create(req, userService.find(p.getId()));
        return ResponseEntity.created(URI.create("/api/v1/courses/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public CourseResponse get(@PathVariable Long id) {
        return courseService.get(id);
    }

    @PostMapping(value = "/{id}/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public CourseResponse uploadPromoVideo(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        return courseService.uploadPromoVideo(id, file, p, userService.find(p.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CourseResponse update(@PathVariable Long id, @RequestBody UpdateCourseRequest req) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        return courseService.update(id, req, p, userService.find(p.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        courseService.delete(id, p, userService.find(p.getId()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        Page<CourseResponse> courses = courseService.list(category, title, search, page, size, sortBy, direction);
        return ResponseEntity.ok(courses);
    }
}
