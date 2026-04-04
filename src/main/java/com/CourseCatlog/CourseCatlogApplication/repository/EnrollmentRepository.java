package com.CourseCatlog.CourseCatlogApplication.repository;

import com.CourseCatlog.CourseCatlogApplication.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUser_IdAndCourse_Id(Long userId, Long courseId);

    List<Enrollment> findByUser_Id(Long userId);

    List<Enrollment> findByCourse_Id(Long courseId);
}
