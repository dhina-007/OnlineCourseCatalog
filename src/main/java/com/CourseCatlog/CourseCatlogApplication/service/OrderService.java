package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.config.properties.CommerceProperties;
import com.CourseCatlog.CourseCatlogApplication.config.properties.StripeProperties;
import com.CourseCatlog.CourseCatlogApplication.dto.CheckoutResponse;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.entity.OrderEntity;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.enums.OrderStatus;
import com.CourseCatlog.CourseCatlogApplication.exception.BadRequestException;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CourseService courseService;
    private final UserService userService;
    private final StripeProperties stripeProperties;
    private final CommerceProperties commerceProperties;

    @Transactional
    public CheckoutResponse checkout(Long courseId, User user) {
        Course course = courseService.find(courseId);
        if (course.isFree()) {
            throw new BadRequestException("Free courses do not require checkout");
        }
        if (orderRepository.existsByUser_IdAndCourse_IdAndStatus(user.getId(), courseId, OrderStatus.PAID)) {
            return new CheckoutResponse(null, null, "ALREADY_PAID");
        }
        OrderEntity order = OrderEntity.builder()
                .user(user)
                .course(course)
                .amount(course.getPrice())
                .currency(course.getCurrency())
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);

        if (commerceProperties.isMockPayments() || !StringUtils.hasText(stripeProperties.getApiKey())) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            log.info("Mock payment: order {} marked PAID for user {} course {}", order.getId(), user.getId(), courseId);
            return new CheckoutResponse(order.getId(), null, "MOCK_PAID");
        }

        Stripe.apiKey = stripeProperties.getApiKey();
        try {
            Map<String, String> meta = new HashMap<>();
            meta.put("orderId", String.valueOf(order.getId()));
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(course.getPrice().movePointRight(2).longValue())
                    .setCurrency(course.getCurrency().toLowerCase())
                    .putAllMetadata(meta)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            order.setStripePaymentIntentId(intent.getId());
            orderRepository.save(order);
            return new CheckoutResponse(order.getId(), intent.getClientSecret(), "REQUIRES_PAYMENT");
        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new com.CourseCatlog.CourseCatlogApplication.exception.TechnicalException("Payment provider error", e);
        }
    }

    @Transactional
    public void confirmStripePayment(String paymentIntentId) {
        if (!StringUtils.hasText(paymentIntentId)) {
            return;
        }
        orderRepository.findByStripePaymentIntentId(paymentIntentId).ifPresent(order -> {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        });
    }

    @Transactional(readOnly = true)
    public OrderEntity findOrderForUser(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .filter(o -> o.getUser().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }
}
