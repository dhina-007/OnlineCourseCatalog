package com.CourseCatlog.CourseCatlogApplication.dto;

public record CheckoutResponse(Long orderId, String clientSecret, String status) {
}
