package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.constant.CourseApiConstants;
import com.CourseCatlog.CourseCatlogApplication.dto.CourseResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.CreateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import com.CourseCatlog.CourseCatlogApplication.service.CourseService;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Validated
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

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<List<CourseResponse>> createBatch(
            @RequestBody
            @Valid
            @Size(min = 1, max = CourseApiConstants.MAX_BATCH_COURSES,
                    message = "Batch must contain between 1 and " + CourseApiConstants.MAX_BATCH_COURSES + " courses")
            List<@Valid CreateCourseRequest> requests) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        List<CourseResponse> created = courseService.createBatch(requests, userService.find(p.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
