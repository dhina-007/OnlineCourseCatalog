package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateLessonRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.LessonResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.MaterialResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateLessonRequest;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.entity.Lesson;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseService courseService;

    public LessonResponse create(Long courseId, CreateLessonRequest req) {
        Course course = courseService.find(courseId);
        Lesson lesson = Lesson.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .duration(req.getDuration())
                .videoUrl(req.getVideoUrl())
                .course(course)
                .build();
        lesson = lessonRepository.save(lesson);
        return map(lesson);
    }

    public List<LessonResponse> listByCourse(Long courseId) {
        courseService.find(courseId);
        return lessonRepository.findByCourseId(courseId).stream().map(this::map).toList();
    }

    public LessonResponse get(Long courseId, Long lessonId) {
        Lesson lesson = find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId))
            throw new NotFoundException("Lesson with id " + lessonId + " not found");
        return map(lesson);
    }

    public LessonResponse update(Long courseId, Long lessonId, UpdateLessonRequest req) {
        Lesson lesson = find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId))
            throw new NotFoundException("Lesson with id " + lessonId + " not found");
        if (req.getTitle() != null) lesson.setTitle(req.getTitle());
        if (req.getContent() != null) lesson.setContent(req.getContent());
        if (req.getDuration() != null) lesson.setDuration(req.getDuration());
        if (req.getVideoUrl() != null) lesson.setVideoUrl(req.getVideoUrl());
        return map(lessonRepository.save(lesson));
    }

    public void delete(Long courseId, Long lessonId) {
        Lesson lesson = find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId))
            throw new NotFoundException("Lesson with id " + lessonId + " not found");
        lessonRepository.delete(lesson);
    }

    public Lesson find(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson with id " + lessonId + " not found"));
    }

    private LessonResponse map(Lesson l) {
        List<MaterialResponse> mats = l.getMaterials() == null ? List.of() :
                l.getMaterials().stream()
                        //.map(m -> MaterialResponse.builder().id(m.getId()).type(m.getType()).url(m.getUrl()).build())
                        .map(m -> new MaterialResponse(m.getId(), m.getType(), m.getUrl()))
                        .toList();

        return new LessonResponse(
                l.getId(),
                l.getTitle(),
                l.getContent(),
                l.getDuration(),
                l.getVideoUrl(),
                mats
        );
    }
}