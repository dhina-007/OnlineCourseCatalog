package com.CourseCatlog.CourseCatlogApplication.controller;
import com.CourseCatlog.CourseCatlogApplication.dto.CreateMaterialRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.MaterialResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateMaterialRequest;
import com.CourseCatlog.CourseCatlogApplication.service.MaterialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/lessons/{lessonId}/materials")
@RequiredArgsConstructor
public class MaterialController {
    private final MaterialService materialService;

    @PostMapping
    public ResponseEntity<MaterialResponse> add(@PathVariable Long courseId,
                                                @PathVariable Long lessonId,
                                                @Valid @RequestBody CreateMaterialRequest req) {
        MaterialResponse created = materialService.add(courseId, lessonId, req);
        return ResponseEntity.created(URI.create(String.format("/courses/%d/lessons/%d/materials/%d",
                courseId, lessonId, created.id()))).body(created);
    }

    @GetMapping
    public List<MaterialResponse> list(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return materialService.list(courseId, lessonId);
    }

    @PutMapping("/{materialId}")
    public MaterialResponse update(@PathVariable Long courseId, @PathVariable Long lessonId,
                                   @PathVariable Long materialId, @RequestBody UpdateMaterialRequest req) {
        return materialService.update(courseId, lessonId, materialId, req);
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> delete(@PathVariable Long courseId,
                                       @PathVariable Long lessonId,
                                       @PathVariable Long materialId) {
        materialService.delete(courseId, lessonId, materialId);
        return ResponseEntity.noContent().build();
    }
}