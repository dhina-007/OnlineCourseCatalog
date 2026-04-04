package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.CreateReviewRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.ReviewResponse;
import com.CourseCatlog.CourseCatlogApplication.security.SecurityUtils;
import com.CourseCatlog.CourseCatlogApplication.service.ReviewService;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@PathVariable Long courseId,
                                                 @Valid @RequestBody CreateReviewRequest req) {
        var p = SecurityUtils.requirePrincipal();
        ReviewResponse r = reviewService.create(courseId, userService.find(p.getId()), req);
        return ResponseEntity.created(URI.create("/api/v1/courses/" + courseId + "/reviews/" + r.id())).body(r);
    }

    @GetMapping
    public List<ReviewResponse> list(@PathVariable Long courseId) {
        return reviewService.listByCourse(courseId);
    }
}
