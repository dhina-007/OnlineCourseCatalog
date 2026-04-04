package com.CourseCatlog.CourseCatlogApplication.repository;

import com.CourseCatlog.CourseCatlogApplication.constant.CourseSql;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitleIgnoreCase(String title);

    @EntityGraph("Course.withInstructor")
    Page<Course> findByCategoryIgnoreCase(String category, Pageable pageable);

    @EntityGraph("Course.withInstructor")
    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @EntityGraph("Course.withInstructor")
    Page<Course> findByCategoryIgnoreCaseAndTitleContainingIgnoreCase(String category, String title, Pageable pageable);

    @EntityGraph("Course.withInstructor")
    Page<Course> findByCategory(String category, Pageable pageable);

    @EntityGraph("Course.withInstructor")
    Page<Course> findByCategoryAndTitleContainingIgnoreCase(String category, String title, Pageable pageable);

    @EntityGraph("Course.withInstructor")
    Page<Course> findAll(Pageable pageable);

    @Query(value = CourseSql.SEARCH_FULLTEXT, countQuery = CourseSql.SEARCH_FULLTEXT_COUNT, nativeQuery = true)
    Page<Course> searchFullText(@Param("q") String q, Pageable pageable);
}
