package com.CourseCatlog.CourseCatlogApplication.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Async
    public void onEnrolled(Long userId, Long courseId) {
        log.info("Enrollment notification: userId={} courseId={}", userId, courseId);
    }
}
