package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import com.CourseCatlog.CourseCatlogApplication.exception.ForbiddenException;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import org.springframework.stereotype.Component;

@Component
public class CourseAuthorizationService {

    public void assertCanEditCourse(Course course, UserPrincipal actor) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (!course.getInstructor().getId().equals(actor.getId())) {
            throw new ForbiddenException("You cannot modify this course content");
        }
    }
}
