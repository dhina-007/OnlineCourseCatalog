package com.CourseCatlog.CourseCatlogApplication.controller;
import com.CourseCatlog.CourseCatlogApplication.dto.CreateLessonRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.LessonResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateLessonRequest;
import com.CourseCatlog.CourseCatlogApplication.service.LessonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<LessonResponse> create(@PathVariable Long courseId,
                                                 @Valid @RequestBody CreateLessonRequest req) {
        LessonResponse created = lessonService.create(courseId, req);
        return ResponseEntity.created(URI.create("/courses/" + courseId + "/lessons/" + created.id()))
                .body(created);
    }

    @GetMapping
    public List<LessonResponse> list(@PathVariable Long courseId) {
        return lessonService.listByCourse(courseId);
    }

    @GetMapping("/{lessonId}")
    public LessonResponse get(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return lessonService.get(courseId, lessonId);
    }

    @PutMapping("/{lessonId}")
    public LessonResponse update(@PathVariable Long courseId,
                                 @PathVariable Long lessonId,
                                 @RequestBody UpdateLessonRequest req) {
        return lessonService.update(courseId, lessonId, req);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId, @PathVariable Long lessonId) {
        lessonService.delete(courseId, lessonId);
        return ResponseEntity.noContent().build();
    }
}