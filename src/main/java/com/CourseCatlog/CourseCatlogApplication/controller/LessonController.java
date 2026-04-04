package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateLessonRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.LessonResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateLessonRequest;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import com.CourseCatlog.CourseCatlogApplication.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<LessonResponse> create(@PathVariable Long courseId,
                                                 @Valid @RequestBody CreateLessonRequest req) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        LessonResponse created = lessonService.create(courseId, req, p);
        return ResponseEntity.created(URI.create("/api/v1/courses/" + courseId + "/lessons/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<LessonResponse> list(@PathVariable Long courseId) {
        return lessonService.listByCourse(courseId, currentPrincipalOrNull());
    }

    @GetMapping("/{lessonId}")
    public LessonResponse get(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return lessonService.get(courseId, lessonId, currentPrincipalOrNull());
    }

    @PutMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public LessonResponse update(@PathVariable Long courseId,
                                 @PathVariable Long lessonId,
                                 @RequestBody UpdateLessonRequest req) {
        return lessonService.update(courseId, lessonId, req, SecurityUtils.requirePrincipal());
    }

    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long courseId, @PathVariable Long lessonId) {
        lessonService.delete(courseId, lessonId, SecurityUtils.requirePrincipal());
        return ResponseEntity.noContent().build();
    }

    private static UserPrincipal currentPrincipalOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal p) {
            return p;
        }
        return null;
    }
}
