package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateMaterialRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.MaterialResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateMaterialRequest;
import com.CourseCatlog.CourseCatlogApplication.entity.Lesson;
import com.CourseCatlog.CourseCatlogApplication.entity.Material;
import com.CourseCatlog.CourseCatlogApplication.entity.MaterialType;
import com.CourseCatlog.CourseCatlogApplication.exception.BadRequestException;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final LessonService lessonService;

    public MaterialResponse add(Long courseId, Long lessonId, CreateMaterialRequest req) {
        if (req.getType() != MaterialType.PDF && req.getType() != MaterialType.VIDEO) {
            throw new BadRequestException("Invalid material type (allowed: PDF, VIDEO)");
        }
        Lesson lesson = lessonService.find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId))
            throw new NotFoundException("Lesson with id " + lessonId + " not found");
        Material m = Material.builder()
                .type(req.getType())
                .url(req.getUrl())
                .lesson(lesson)
                .build();
        return map(materialRepository.save(m));
    }

    public List<MaterialResponse> list(Long courseId, Long lessonId) {
        Lesson lesson = lessonService.find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId))
            throw new NotFoundException("Lesson with id " + lessonId + " not found");
        return materialRepository.findByLessonId(lessonId).stream().map(this::map).toList();
    }

    public void delete(Long courseId, Long lessonId, Long materialId) {
        Material m = find(materialId);
        if (!m.getLesson().getId().equals(lessonId) || !m.getLesson().getCourse().getId().equals(courseId))
            throw new NotFoundException("Material with id " + materialId + " not found");
        materialRepository.delete(m);
    }

    public MaterialResponse update(Long courseId, Long lessonId, Long materialId, UpdateMaterialRequest req) {
        Material m = find(materialId);
        if (!m.getLesson().getId().equals(lessonId) || !m.getLesson().getCourse().getId().equals(courseId))
            throw new NotFoundException("Material with id " + materialId + " not found");
        if (req.getType() != null) {
            if (req.getType() != MaterialType.PDF && req.getType() != MaterialType.VIDEO)
                throw new BadRequestException("Invalid material type (allowed: PDF, VIDEO)");
            m.setType(req.getType());
        }
        if (req.getUrl() != null) m.setUrl(req.getUrl());
        return map(materialRepository.save(m));
    }

    private Material find(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Material with id " + id + " not found"));
    }

    private MaterialResponse map(Material m) {
        return MaterialResponse.builder()
                .id(m.getId())
                .type(m.getType())
                .url(m.getUrl())
                .build();
    }
}