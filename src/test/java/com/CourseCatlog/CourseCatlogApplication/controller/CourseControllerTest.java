package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.dto.CourseResponse;
import com.CourseCatlog.CourseCatlogApplication.security.JwtAuthenticationFilter;
import com.CourseCatlog.CourseCatlogApplication.service.CourseService;
import com.CourseCatlog.CourseCatlogApplication.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CourseService courseService;

    @MockBean
    UserService userService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void list_givenNoParams_shouldReturn200() throws Exception {
        CourseResponse row = CourseResponse.builder()
                .id(1L)
                .title("Java")
                .description("desc")
                .category("Dev")
                .instructorName("Alice")
                .language("en")
                .createdAt(Instant.now())
                .price(BigDecimal.ZERO)
                .currency("USD")
                .free(true)
                .tags(Set.of())
                .averageRating(null)
                .videoUrl(null)
                .imageUrl(null)
                .build();
        Page<CourseResponse> page = new PageImpl<>(List.of(row), PageRequest.of(0, 10), 1);
        when(courseService.list(isNull(), isNull(), isNull(), eq(0), eq(10), isNull(), isNull())).thenReturn(page);

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Java"));
    }
}
