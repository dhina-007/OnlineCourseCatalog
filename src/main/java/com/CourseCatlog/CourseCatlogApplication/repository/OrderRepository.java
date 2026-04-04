package com.CourseCatlog.CourseCatlogApplication.repository;

import com.CourseCatlog.CourseCatlogApplication.entity.OrderEntity;
import com.CourseCatlog.CourseCatlogApplication.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    boolean existsByUser_IdAndCourse_IdAndStatus(Long userId, Long courseId, OrderStatus status);

    Optional<OrderEntity> findByStripePaymentIntentId(String stripePaymentIntentId);
}
