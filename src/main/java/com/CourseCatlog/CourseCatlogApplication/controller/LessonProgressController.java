package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.LessonProgressResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateLessonProgressRequest;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.service.LessonProgressService;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}")
@RequiredArgsConstructor
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;
    private final UserService userService;

    @PutMapping("/lessons/{lessonId}/progress")
    public LessonProgressResponse upsert(@PathVariable Long courseId,
                                         @PathVariable Long lessonId,
                                         @Valid @RequestBody UpdateLessonProgressRequest req) {
        var p = SecurityUtils.requirePrincipal();
        return lessonProgressService.upsert(courseId, lessonId, userService.find(p.getId()), req);
    }

    @GetMapping("/progress")
    public List<LessonProgressResponse> list(@PathVariable Long courseId) {
        var p = SecurityUtils.requirePrincipal();
        return lessonProgressService.listForCourse(courseId, userService.find(p.getId()));
    }
}
