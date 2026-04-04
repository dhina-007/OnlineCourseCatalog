package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateReviewRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.ReviewResponse;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.entity.Review;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.exception.ConflictException;
import com.CourseCatlog.CourseCatlogApplication.exception.ForbiddenException;
import com.CourseCatlog.CourseCatlogApplication.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @Transactional
    public ReviewResponse create(Long courseId, User user, CreateReviewRequest req) {
        Course course = courseService.find(courseId);
        if (!enrollmentService.isEnrolled(user.getId(), courseId)) {
            throw new ForbiddenException("Only enrolled students can review a course");
        }
        if (reviewRepository.findByUser_IdAndCourse_Id(user.getId(), courseId).isPresent()) {
            throw new ConflictException("You already reviewed this course");
        }
        Review r = Review.builder()
                .user(user)
                .course(course)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();
        return map(reviewRepository.save(r));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listByCourse(Long courseId) {
        courseService.find(courseId);
        return reviewRepository.findByCourse_IdOrderByCreatedAtDesc(courseId).stream()
                .map(this::map)
                .toList();
    }

    private ReviewResponse map(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .courseId(r.getCourse().getId())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
