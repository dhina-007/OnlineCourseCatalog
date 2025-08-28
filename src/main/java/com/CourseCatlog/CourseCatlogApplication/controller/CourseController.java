package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.CourseResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.CreateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.service.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CreateCourseRequest req) {
        CourseResponse created = courseService.create(req);
        return ResponseEntity.created(URI.create("/courses/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public CourseResponse get(@PathVariable Long id) {
        return courseService.get(id);
    }

    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable Long id, @RequestBody UpdateCourseRequest req) {
        return courseService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Course>> getCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.getCourses(pageable, category, title);
        return ResponseEntity.ok(courses);
    }

}