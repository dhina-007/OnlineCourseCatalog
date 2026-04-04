package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.dto.CourseResponse;
import com.CourseCatlog.CourseCatlogApplication.dto.CreateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.dto.UpdateCourseRequest;
import com.CourseCatlog.CourseCatlogApplication.entity.Course;
import com.CourseCatlog.CourseCatlogApplication.entity.Tag;
import com.CourseCatlog.CourseCatlogApplication.entity.User;
import com.CourseCatlog.CourseCatlogApplication.enums.Role;
import com.CourseCatlog.CourseCatlogApplication.exception.ConflictException;
import com.CourseCatlog.CourseCatlogApplication.exception.ForbiddenException;
import com.CourseCatlog.CourseCatlogApplication.exception.NotFoundException;
import com.CourseCatlog.CourseCatlogApplication.repository.CourseRepository;
import com.CourseCatlog.CourseCatlogApplication.repository.ReviewRepository;
import com.CourseCatlog.CourseCatlogApplication.repository.TagRepository;
import com.CourseCatlog.CourseCatlogApplication.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;
    private final CourseVideoStorageService courseVideoStorageService;

    @Transactional
    @CacheEvict(cacheNames = "courseCatalog", allEntries = true)
    public CourseResponse create(CreateCourseRequest req, User instructor) {
        if (courseRepository.existsByTitleIgnoreCase(req.getTitle())) {
            throw new ConflictException("Course title already exists");
        }
        Course c = Course.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .instructor(instructor)
                .language(req.getLanguage())
                .price(req.getPrice() != null ? req.getPrice() : BigDecimal.ZERO)
                .currency(req.getCurrency() != null ? req.getCurrency() : "USD")
                .free(req.getFree() == null || Boolean.TRUE.equals(req.getFree()))
                .build();
        if (!c.isFree() && c.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new com.CourseCatlog.CourseCatlogApplication.exception.BadRequestException(
                    "Paid courses must have price greater than zero");
        }
        if (StringUtils.hasText(req.getVideoUrl())) {
            c.setVideoUrl(req.getVideoUrl());
        }
        applyTags(c, req.getTagNames());
        c = courseRepository.save(c);
        return map(c, true);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "courseCatalog",
            key = "#page + '-' + #size + '-' + #category + '-' + #title + '-' + #search + '-' + #sortBy + '-' + #direction",
            unless = "#search != null && !#search.isEmpty()"
    )
    public Page<CourseResponse> list(
            String category,
            String title,
            String search,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = Sort.by("id").ascending();
        if (StringUtils.hasText(sortBy)) {
            sort = "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Course> result;
        if (StringUtils.hasText(search)) {
            result = courseRepository.searchFullText(search.trim(), pageable);
        } else {
            boolean hasCategory = StringUtils.hasText(category);
            boolean hasTitle = StringUtils.hasText(title);
            if (hasCategory && hasTitle) {
                result = courseRepository.findByCategoryIgnoreCaseAndTitleContainingIgnoreCase(
                        category.trim(), title.trim(), pageable);
            } else if (hasCategory) {
                result = courseRepository.findByCategoryIgnoreCase(category.trim(), pageable);
            } else if (hasTitle) {
                result = courseRepository.findByTitleContainingIgnoreCase(title.trim(), pageable);
            } else {
                result = courseRepository.findAll(pageable);
            }
        }
        return result.map(c -> map(c, false));
    }

    @Transactional(readOnly = true)
    public CourseResponse get(Long id) {
        return map(find(id), true);
    }

    @Transactional
    @CacheEvict(cacheNames = "courseCatalog", allEntries = true)
    public CourseResponse update(Long id, UpdateCourseRequest req, UserPrincipal actor, User actorUser) {
        Course c = find(id);
        assertCanModify(c, actor, actorUser);
        if (StringUtils.hasText(req.getTitle())) {
            if (!c.getTitle().equalsIgnoreCase(req.getTitle())
                    && courseRepository.existsByTitleIgnoreCase(req.getTitle())) {
                throw new ConflictException("Course title already exists");
            }
            c.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            c.setDescription(req.getDescription());
        }
        if (StringUtils.hasText(req.getCategory())) {
            c.setCategory(req.getCategory());
        }
        if (StringUtils.hasText(req.getLanguage())) {
            c.setLanguage(req.getLanguage());
        }
        if (req.getPrice() != null) {
            c.setPrice(req.getPrice());
        }
        if (req.getCurrency() != null) {
            c.setCurrency(req.getCurrency());
        }
        if (req.getFree() != null) {
            c.setFree(req.getFree());
        }
        if (!c.isFree() && c.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new com.CourseCatlog.CourseCatlogApplication.exception.BadRequestException(
                    "Paid courses must have price greater than zero");
        }
        if (req.getTagNames() != null) {
            applyTags(c, req.getTagNames());
        }
        if (req.getVideoUrl() != null) {
            c.setVideoUrl(StringUtils.hasText(req.getVideoUrl()) ? req.getVideoUrl() : null);
        }
        return map(courseRepository.save(c), true);
    }

    @Transactional
    @CacheEvict(cacheNames = "courseCatalog", allEntries = true)
    public CourseResponse uploadPromoVideo(Long courseId, MultipartFile file, UserPrincipal actor, User actorUser) {
        Course c = find(courseId);
        assertCanModify(c, actor, actorUser);
        String url = courseVideoStorageService.uploadCourseVideo(courseId, file);
        c.setVideoUrl(url);
        return map(courseRepository.save(c), true);
    }

    @Transactional
    @CacheEvict(cacheNames = "courseCatalog", allEntries = true)
    public void delete(Long id, UserPrincipal actor, User actorUser) {
        Course c = find(id);
        assertCanModify(c, actor, actorUser);
        courseRepository.delete(c);
    }

    @Transactional(readOnly = true)
    public Course find(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course with id " + id + " not found"));
    }

    private void assertCanModify(Course course, UserPrincipal actor, User actorUser) {
        boolean admin = actor.getRole() == Role.ADMIN;
        boolean owner = course.getInstructor().getId().equals(actor.getId());
        if (!admin && !owner) {
            throw new ForbiddenException("You cannot modify this course");
        }
    }

    private void applyTags(Course c, Set<String> tagNames) {
        if (tagNames == null) {
            return;
        }
        c.getTags().clear();
        for (String raw : tagNames) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            String name = raw.trim();
            Tag tag = tagRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));
            c.getTags().add(tag);
        }
    }

    private CourseResponse map(Course c, boolean includeAverageRating) {
        Set<String> tagNames = c.getTags() == null ? Set.of() : c.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toCollection(HashSet::new));
        Double avg = null;
        if (includeAverageRating) {
            avg = reviewRepository.averageRatingByCourseId(c.getId()).orElse(null);
        }
        return CourseResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .instructorName(c.getInstructor().getName())
                .language(c.getLanguage())
                .createdAt(c.getCreatedAt())
                .price(c.getPrice())
                .currency(c.getCurrency())
                .free(c.isFree())
                .tags(tagNames)
                .averageRating(avg)
                .videoUrl(c.getVideoUrl())
                .build();
    }
}
