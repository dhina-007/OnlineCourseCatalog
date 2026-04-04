package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.LessonProgressResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateLessonProgressRequest;
import com.CourseCatlog.CourseCatlogApplication.entity.Lesson;
import com.CourseCatlog.CourseCatlogApplication.entity.LessonProgress;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.exception.ForbiddenException;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final LessonService lessonService;
    private final EnrollmentService enrollmentService;

    @Transactional
    public LessonProgressResponse upsert(Long courseId, Long lessonId, User user, UpdateLessonProgressRequest req) {
        if (!enrollmentService.isEnrolled(user.getId(), courseId)) {
            throw new ForbiddenException("Must be enrolled to track progress");
        }
        Lesson lesson = lessonService.find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new NotFoundException("Lesson not found in this course");
        }
        LessonProgress p = lessonProgressRepository.findByUser_IdAndLesson_Id(user.getId(), lessonId)
                .orElseGet(() -> LessonProgress.builder().user(user).lesson(lesson).build());
        if (req.getCompleted() != null) {
            p.setCompleted(req.getCompleted());
        }
        if (req.getProgressPercent() != null) {
            p.setProgressPercent(req.getProgressPercent());
        }
        if (req.getLastPositionSec() != null) {
            p.setLastPositionSec(req.getLastPositionSec());
        }
        return map(lessonProgressRepository.save(p));
    }

    @Transactional(readOnly = true)
    public List<LessonProgressResponse> listForCourse(Long courseId, User user) {
        if (!enrollmentService.isEnrolled(user.getId(), courseId)) {
            throw new ForbiddenException("Must be enrolled to view progress");
        }
        return lessonProgressRepository.findByUser_IdAndLesson_Course_Id(user.getId(), courseId).stream()
                .map(this::map)
                .toList();
    }

    private LessonProgressResponse map(LessonProgress p) {
        return LessonProgressResponse.builder()
                .lessonId(p.getLesson().getId())
                .completed(p.isCompleted())
                .progressPercent(p.getProgressPercent())
                .lastPositionSec(p.getLastPositionSec())
                .build();
    }
}
