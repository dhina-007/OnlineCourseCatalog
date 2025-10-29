package com.CourseCatlog.CourseCatlogApplication.service;
import com.CourseCatlog.CourseCatlogApplication.dto.CourseResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.CreateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.exception.ConflictException;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseResponse create(CreateCourseRequest req) {
        if (courseRepository.existsByTitleIgnoreCase(req.getTitle()))
            throw new ConflictException("Course title already exists");
        Course c = Course.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .instructor(req.getInstructor())
                .language(req.getLanguage())
                .build();
        c = courseRepository.save(c);
        return map(c);
    }

    public Page<CourseResponse> list(String category, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Course> result;
        boolean hasCategory = StringUtils.hasText(category);
        boolean hasTitle = StringUtils.hasText(title);
        if (hasCategory && hasTitle) {
            result = courseRepository.findByCategoryIgnoreCaseAndTitleContainingIgnoreCase(category.trim(), title.trim(), pageable);
        } else if (hasCategory) {
            result = courseRepository.findByCategoryIgnoreCase(category.trim(), pageable);
        } else if (hasTitle) {
            result = courseRepository.findByTitleContainingIgnoreCase(title.trim(), pageable);
        } else {
            result = courseRepository.findAll(pageable);
        }
        return result.map(this::map);
    }

    public CourseResponse get(Long id) {
        return map(find(id));
    }

    public CourseResponse update(Long id, UpdateCourseRequest req) {
        Course c = find(id);
        if (StringUtils.hasText(req.getTitle())) {
            if (!c.getTitle().equalsIgnoreCase(req.getTitle())
                    && courseRepository.existsByTitleIgnoreCase(req.getTitle()))
                throw new ConflictException("Course title already exists");
            c.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) c.setDescription(req.getDescription());
        if (StringUtils.hasText(req.getCategory())) c.setCategory(req.getCategory());
        if (StringUtils.hasText(req.getInstructor())) c.setInstructor(req.getInstructor());
        if (StringUtils.hasText(req.getLanguage())) c.setLanguage(req.getLanguage());
        return map(courseRepository.save(c));
    }

    public void delete(Long id) {
        Course c = find(id);
        courseRepository.delete(c);
    }

    public Course find(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course with id " + id + " not found"));
    }

    private CourseResponse map(Course c) {
        return CourseResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .instructor(c.getInstructor())
                .language(c.getLanguage())
                .createdAt(c.getCreatedAt())
                .build();
    }

    public Page<Course> getCourses(Pageable pageable, String category, String title) {
        if (category != null && title != null) {
            return courseRepository.findByCategoryAndTitleContainingIgnoreCase(category, title, pageable);
        } else if (category != null) {
            return courseRepository.findByCategory(category, pageable);
        } else if (title != null) {
            return courseRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            return courseRepository.findAll(pageable);
        }
    }

    public Page<CourseResponse> list(String category, String title, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by("id").ascending(); // default sort
        if (StringUtils.hasText(sortBy)) {
            if ("desc".equalsIgnoreCase(direction)) {
                sort = Sort.by(sortBy).descending();
            } else {
                sort = Sort.by(sortBy).ascending();
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Course> result;
        boolean hasCategory = StringUtils.hasText(category);
        boolean hasTitle = StringUtils.hasText(title);

        if (hasCategory && hasTitle) {
            result = courseRepository.findByCategoryIgnoreCaseAndTitleContainingIgnoreCase(category.trim(), title.trim(), pageable);
        } else if (hasCategory) {
            result = courseRepository.findByCategoryIgnoreCase(category.trim(), pageable);
        } else if (hasTitle) {
            result = courseRepository.findByTitleContainingIgnoreCase(title.trim(), pageable);
        } else {
            result = courseRepository.findAll(pageable);
        }

        return result.map(this::map);
    }
}