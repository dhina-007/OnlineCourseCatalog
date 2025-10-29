package com.CourseCatlog.CourseCatlogApplication.repository;

import com.CourseCatlog.CourseCatlogApplication.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByLessonId(Long lessonId);
}