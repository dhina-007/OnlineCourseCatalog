package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.EnrollmentResponse;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.entity.Enrollment;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.enums.OrderStatus;
import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import com.CourseCatlog.CourseCatlogApplication.exception.ConflictException;
import com.CourseCatlog.CourseCatlogApplication.exception.ForbiddenException;
import com.CourseCatlog.CourseCatlogApplication.repository.EnrollmentRepository;
import com.CourseCatlog.CourseCatlogApplication.repository.OrderRepository;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final NotificationService notificationService;

    @Transactional
    public EnrollmentResponse enroll(Long courseId, User user) {
        Course course = courseService.find(courseId);
        if (!course.isFree()) {
            boolean paid = orderRepository.existsByUser_IdAndCourse_IdAndStatus(
                    user.getId(), courseId, OrderStatus.PAID);
            if (!paid) {
                throw new ForbiddenException("Complete purchase before enrolling in this course");
            }
        }
        if (enrollmentRepository.existsByUser_IdAndCourse_Id(user.getId(), course.getId())) {
            throw new ConflictException("User already enrolled in course");
        }
        Enrollment e = enrollmentRepository.save(
                Enrollment.builder().user(user).course(course).build());
        notificationService.onEnrolled(user.getId(), courseId);
        return map(e);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listByUser(Long userId, UserPrincipal actor) {
        if (!actor.getId().equals(userId) && actor.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Cannot view other users' enrollments");
        }
        userService.find(userId);
        return enrollmentRepository.findByUser_Id(userId).stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public List<User> studentsInCourse(Long courseId, UserPrincipal actor) {
        Course course = courseService.find(courseId);
        boolean admin = actor.getRole() == Role.ADMIN;
        boolean owner = course.getInstructor().getId().equals(actor.getId());
        if (!admin && !owner) {
            throw new ForbiddenException("Cannot list students for this course");
        }
        return enrollmentRepository.findByCourse_Id(courseId).stream().map(Enrollment::getUser).toList();
    }

    @Transactional(readOnly = true)
    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUser_IdAndCourse_Id(userId, courseId);
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
