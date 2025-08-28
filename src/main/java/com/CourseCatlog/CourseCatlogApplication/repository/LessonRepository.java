package com.CourseCatlog.CourseCatlogApplication.repository;

import com.CourseCatlog.CourseCatlogApplication.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseId(Long courseId);
}