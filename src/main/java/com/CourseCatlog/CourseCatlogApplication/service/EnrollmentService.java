package com.CourseCatlog.CourseCatlogApplication.service;
import com.CourseCatlog.CourseCatlogApplication.dto.EnrollmentResponse;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.entity.Enrollment;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.exception.ConflictException;
import com.CourseCatlog.CourseCatlogApplication.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseService courseService;

    public EnrollmentResponse enroll(Long userId, Long courseId) {
        User user = userService.find(userId);
        Course course = courseService.find(courseId);
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId()))
            throw new ConflictException("User already enrolled in course");
        Enrollment e = enrollmentRepository.save(Enrollment.builder().user(user).course(course).build());
        return map(e);
    }

    public List<EnrollmentResponse> listByUser(Long userId) {
        userService.find(userId);
        return enrollmentRepository.findByUserId(userId).stream().map(this::map).toList();
    }

    public List<User> studentsInCourse(Long courseId) {
        courseService.find(courseId);
        return enrollmentRepository.findByCourseId(courseId).stream().map(Enrollment::getUser).toList();
    }

    private EnrollmentResponse map(Enrollment e) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .userId(e.getUser().getId())
                .courseId(e.getCourse().getId())
                .enrolledAt(e.getEnrolledAt())
                .build();
    }
}