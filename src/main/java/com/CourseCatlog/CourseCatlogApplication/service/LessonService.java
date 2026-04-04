package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateLessonRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.LessonResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.MaterialResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateLessonRequest;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.entity.Lesson;
import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.LessonRepository;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ContentUrlService contentUrlService;
    private final CourseAuthorizationService courseAuthorizationService;

    @Transactional
    public LessonResponse create(Long courseId, CreateLessonRequest req, UserPrincipal editor) {
        Course course = courseService.find(courseId);
        courseAuthorizationService.assertCanEditCourse(course, editor);
        Lesson lesson = Lesson.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .duration(req.getDuration())
                .videoUrl(req.getVideoUrl())
                .course(course)
                .build();
        lesson = lessonRepository.save(lesson);
        return map(lesson, true, lesson.getCourse().getInstructor().getId());
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> listByCourse(Long courseId, UserPrincipal viewer) {
        courseService.find(courseId);
        boolean reveal = canReveal(courseId, viewer);
        long signer = viewer != null ? viewer.getId() : 0L;
        return lessonRepository.findByCourseId(courseId).stream()
                .map(l -> map(l, reveal, signer))
                .toList();
    }

    @Transactional(readOnly = true)
    public LessonResponse get(Long courseId, Long lessonId, UserPrincipal viewer) {
        Lesson lesson = find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new NotFoundException("Lesson with id " + lessonId + " not found");
        }
        boolean reveal = canReveal(courseId, viewer);
        long signer = viewer != null ? viewer.getId() : 0L;
        return map(lesson, reveal, signer);
    }

    @Transactional
    public LessonResponse update(Long courseId, Long lessonId, UpdateLessonRequest req, UserPrincipal editor) {
        Lesson lesson = find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new NotFoundException("Lesson with id " + lessonId + " not found");
        }
        courseAuthorizationService.assertCanEditCourse(lesson.getCourse(), editor);
        if (req.getTitle() != null) {
            lesson.setTitle(req.getTitle());
        }
        if (req.getContent() != null) {
            lesson.setContent(req.getContent());
        }
        if (req.getDuration() != null) {
            lesson.setDuration(req.getDuration());
        }
        if (req.getVideoUrl() != null) {
            lesson.setVideoUrl(req.getVideoUrl());
        }
        return map(lessonRepository.save(lesson), true, lesson.getCourse().getInstructor().getId());
    }

    @Transactional
    public void delete(Long courseId, Long lessonId, UserPrincipal editor) {
        Lesson lesson = find(lessonId);
        if (!lesson.getCourse().getId().equals(courseId)) {
            throw new NotFoundException("Lesson with id " + lessonId + " not found");
        }
        courseAuthorizationService.assertCanEditCourse(lesson.getCourse(), editor);
        lessonRepository.delete(lesson);
    }

    @Transactional(readOnly = true)
    public Lesson find(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson with id " + lessonId + " not found"));
    }

    private boolean canReveal(Long courseId, UserPrincipal viewer) {
        if (viewer == null) {
            return false;
        }
        if (viewer.getRole() == Role.ADMIN) {
            return true;
        }
        Course c = courseService.find(courseId);
        if (c.getInstructor().getId().equals(viewer.getId())) {
            return true;
        }
        return enrollmentService.isEnrolled(viewer.getId(), courseId);
    }

    private LessonResponse map(Lesson l, boolean reveal, long signingUserId) {
        long exp = Instant.now().getEpochSecond() + 3600;
        String video = null;
        List<MaterialResponse> mats;
        if (reveal) {
            video = l.getVideoUrl() != null
                    ? contentUrlService.signUrl(l.getVideoUrl(), signingUserId, exp)
                    : null;
            mats = l.getMaterials() == null ? List.of() :
                    l.getMaterials().stream()
                            .map(m -> new MaterialResponse(
                                    m.getId(),
                                    m.getType(),
                                    contentUrlService.signUrl(m.getUrl(), signingUserId, exp)))
                            .toList();
        } else {
            mats = l.getMaterials() == null ? List.of() :
                    l.getMaterials().stream()
                            .map(m -> new MaterialResponse(m.getId(), m.getType(), null))
                            .toList();
        }
        String content = reveal ? l.getContent() : null;
        return new LessonResponse(
                l.getId(),
                l.getTitle(),
                content,
                l.getDuration(),
                video,
                mats
        );
    }
}
