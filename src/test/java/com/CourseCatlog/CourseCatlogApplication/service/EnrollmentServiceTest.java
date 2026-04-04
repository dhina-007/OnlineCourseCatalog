package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import com.CourseCatlog.CourseCatlogApplication.exception.ForbiddenException;
import com.CourseCatlog.CourseCatlogApplication.repository.EnrollmentRepository;
import com.CourseCatlog.CourseCatlogApplication.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    EnrollmentRepository enrollmentRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    UserService userService;

    @Mock
    CourseService courseService;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    EnrollmentService enrollmentService;

    @Test
    void enroll_givenPaidCourseWithoutOrder_shouldThrowForbidden() {
        User u = User.builder().id(1L).name("a").email("a@b.c").passwordHash("x").role(Role.STUDENT).build();
        User instructor = User.builder().id(2L).name("i").email("i@b.c").passwordHash("x").role(Role.INSTRUCTOR).build();
        Course c = Course.builder()
                .id(10L)
                .title("Paid")
                .category("x")
                .instructor(instructor)
                .language("en")
                .free(false)
                .price(BigDecimal.TEN)
                .currency("USD")
                .build();
        when(courseService.find(10L)).thenReturn(c);
        when(orderRepository.existsByUser_IdAndCourse_IdAndStatus(1L, 10L,
                com.CourseCatlog.CourseCatlogApplication.enums.OrderStatus.PAID)).thenReturn(false);

        assertThatThrownBy(() -> enrollmentService.enroll(10L, u))
                .isInstanceOf(ForbiddenException.class);

        verify(enrollmentRepository, never()).save(any());
    }
}
