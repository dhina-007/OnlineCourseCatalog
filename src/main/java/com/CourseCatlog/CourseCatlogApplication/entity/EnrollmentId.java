package com.CourseCatlog.CourseCatlogApplication.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EnrollmentId implements Serializable {
    private Long userId;
    private Long courseId;

    public EnrollmentId() {}
    public EnrollmentId(Long userId, Long courseId) {
        this.userId = userId; this.courseId = courseId;
    }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnrollmentId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(courseId, that.courseId);
    }
    @Override public int hashCode() { return Objects.hash(userId, courseId); }
}