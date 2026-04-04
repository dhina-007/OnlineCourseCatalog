package com.CourseCatlog.CourseCatlogApplication.repository;

import com.CourseCatlog.CourseCatlogApplication.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByUser_IdAndLesson_Id(Long userId, Long lessonId);

    List<LessonProgress> findByUser_IdAndLesson_Course_Id(Long userId, Long courseId);
}
