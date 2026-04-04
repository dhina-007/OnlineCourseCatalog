package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateMaterialRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.MaterialResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateMaterialRequest;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import com.CourseCatlog.CourseCatlogApplication.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons/{lessonId}/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<MaterialResponse> add(@PathVariable Long courseId,
                                                @PathVariable Long lessonId,
                                                @Valid @RequestBody CreateMaterialRequest req) {
        UserPrincipal p = SecurityUtils.requirePrincipal();
        MaterialResponse created = materialService.add(courseId, lessonId, req, p);
        return ResponseEntity.created(URI.create(String.format(
                "/api/v1/courses/%d/lessons/%d/materials/%d", courseId, lessonId, created.id()))).body(created);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<MaterialResponse> list(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return materialService.list(courseId, lessonId);
    }

    @PutMapping("/{materialId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public MaterialResponse update(@PathVariable Long courseId, @PathVariable Long lessonId,
                                   @PathVariable Long materialId, @RequestBody UpdateMaterialRequest req) {
        return materialService.update(courseId, lessonId, materialId, req, SecurityUtils.requirePrincipal());
    }

    @DeleteMapping("/{materialId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long courseId,
                                       @PathVariable Long lessonId,
                                       @PathVariable Long materialId) {
        materialService.delete(courseId, lessonId, materialId, SecurityUtils.requirePrincipal());
        return ResponseEntity.noContent().build();
    }
}
