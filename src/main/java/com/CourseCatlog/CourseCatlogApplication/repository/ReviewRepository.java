package com.CourseCatlog.CourseCatlogApplication.repository;

import com.CourseCatlog.CourseCatlogApplication.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCourse_IdOrderByCreatedAtDesc(Long courseId);

    Optional<Review> findByUser_IdAndCourse_Id(Long userId, Long courseId);

    @Query(value = "SELECT AVG(rating) FROM reviews WHERE course_id = :courseId", nativeQuery = true)
    Optional<Double> averageRatingByCourseId(@Param("courseId") Long courseId);
}
