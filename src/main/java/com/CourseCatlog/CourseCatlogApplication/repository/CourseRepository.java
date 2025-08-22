package com.CourseCatlog.CourseCatlogApplication.repository;

import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitleIgnoreCase(String title);
    Page<Course> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Course> findByCategoryIgnoreCaseAndTitleContainingIgnoreCase(String category, String title, Pageable pageable);
    Page<Course> findByCategory(String category, Pageable pageable);
    Page<Course> findByCategoryAndTitleContainingIgnoreCase(String category, String title, Pageable pageable);
}