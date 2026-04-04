package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.CheckoutResponse;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.service.OrderService;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutBody body) {
        var p = SecurityUtils.requirePrincipal();
        if (body == null || body.courseId() == null) {
            throw new com.CourseCatlog.CourseCatlogApplication.exception.BadRequestException("courseId is required");
        }
        return ResponseEntity.ok(orderService.checkout(body.courseId(), userService.find(p.getId())));
    }

    public record CheckoutBody(Long courseId) {
    }
}
