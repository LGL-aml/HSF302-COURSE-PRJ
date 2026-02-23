package com.jungle.courseshop.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.courseshop.dto.request.CourseCreateRequest;
import com.jungle.courseshop.dto.request.CourseModuleRequest;
import com.jungle.courseshop.dto.request.CourseUpdateRequest;
import com.jungle.courseshop.dto.response.*;
import com.jungle.courseshop.entity.*;
import com.jungle.courseshop.exception.ResourceNotFoundException;
import com.jungle.courseshop.repository.*;
import com.jungle.courseshop.service.CloudinaryService;
import com.jungle.courseshop.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final UserRepo userRepository;
    private final CourseRepo courseRepository;
    private final CourseModuleRepo moduleRepository;
    private final CourseEnrollmentRepo enrollmentRepository;
    private final CloudinaryService cloudinaryService;
    private final WatchedVideoRepo watchedVideoRepository;
    private final ObjectMapper objectMapper;
    private final TopicRepo topicRepo;


    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has STAFF or MANAGER role
        if (currentUser.getRole() != Role.LECTURER) {
            throw new AccessDeniedException("Only Lecturer can create courses");
        }


        Topic topic = topicRepo.findByIdAndActive(request.getTopicId(), true);
        if(topic == null) {
            throw new ResourceNotFoundException("Topic not found with ID: " + request.getTopicId());
        }
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setTopic(topic);
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO);
        course.setContent(request.getContent());
        course.setDuration(request.getDuration());
        course.setActive(true);
        course.setCreator(currentUser);

        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(request.getCoverImage());
            course.setCoverImage(imageUrl);
        }

        Course savedCourse = courseRepository.save(course);
        log.info("Course created: {}", savedCourse.getTitle());

        // Create modules if provided
        List<CourseModule> modules = new ArrayList<>();


        if (StringUtils.hasText(request.getModules())) {
            List<CourseModuleRequest> moduleRequests = objectMapper.readValue(
                    request.getModules(), new TypeReference<>() {});

            for (CourseModuleRequest moduleRequest : moduleRequests) {
                CourseModule module = new CourseModule();
                module.setTitle(moduleRequest.getTitle());
                module.setOrderIndex(moduleRequest.getOrderIndex());
                module.setCourse(savedCourse);

                List<CourseVideo> videos = new ArrayList<>();
                if (moduleRequest.getVideos() != null) {
                    for (CourseModuleRequest.VideoCourseRequest url : moduleRequest.getVideos()) {
                        CourseVideo video = new CourseVideo();
                        video.setTitle(url.getTitle());
                        video.setVideoUrl(url.getVideoUrl());
                        video.setCourseModule(module);
                        videos.add(video);
                    }
                }
                module.setVideos(videos);
                modules.add(module);
            }
            moduleRepository.saveAll(modules);
        }

        return mapToCourseResponse(savedCourse, modules, currentUser);
    }


    @Transactional
    public CourseResponse updateCourse(Long id, CourseUpdateRequest request) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Chỉ cho phép LECTURER là creator update course
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        if (currentUser.getRole() != Role.LECTURER || !java.util.Objects.equals(course.getCreator().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Only the creator lecturer can update this course");
        }

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getContent() != null) course.setContent(request.getContent());
        if (request.getDuration() != null) course.setDuration(request.getDuration());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getTopicId() != null) {
            Topic topic = topicRepo.findByIdAndActive(request.getTopicId(), true);
            if (topic == null) {
                throw new ResourceNotFoundException("Topic not found with ID: " + request.getTopicId());
            }
            course.setTopic(topic);
        }
        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(request.getCoverImage());
            course.setCoverImage(imageUrl);
        }

        // Xử lý cập nhật modules và videos nếu có
        if (request.getModules() != null) {
            // Xóa toàn bộ module và video cũ
            List<CourseModule> oldModules = moduleRepository.findByCourse_ActiveTrueOrderByOrderIndexAsc()
                .stream().filter(m -> m.getCourse().getId().equals(course.getId())).toList();
            moduleRepository.deleteAll(oldModules);

            // Tạo lại module và video mới
            List<CourseModuleRequest> moduleRequests = objectMapper.readValue(
                    request.getModules(), new TypeReference<>() {});
            List<CourseModule> newModules = new ArrayList<>();
            for (CourseModuleRequest moduleRequest : moduleRequests) {
                CourseModule module = new CourseModule();
                module.setTitle(moduleRequest.getTitle());
                module.setOrderIndex(moduleRequest.getOrderIndex());
                module.setCourse(course);
                List<CourseVideo> videos = new ArrayList<>();
                if (moduleRequest.getVideos() != null) {
                    for (CourseModuleRequest.VideoCourseRequest url : moduleRequest.getVideos()) {
                        CourseVideo video = new CourseVideo();
                        video.setTitle(url.getTitle());
                        video.setVideoUrl(url.getVideoUrl());
                        video.setCourseModule(module);
                        videos.add(video);
                    }
                }
                module.setVideos(videos);
                newModules.add(module);
            }
            moduleRepository.saveAll(newModules);
        }

        Course savedCourse = courseRepository.save(course);
        // Lấy lại modules để trả về response
        List<CourseModule> modules = moduleRepository.findByCourse_ActiveTrueOrderByOrderIndexAsc();
        return mapToCourseResponse(savedCourse, modules, currentUser);
    }

    public List<CourseResponse> getCreatedCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        List<Course> courses = courseRepository.findByCreatorAndActiveTrue(currentUser);
        if (courses.isEmpty()) {
            throw new ResourceNotFoundException("No courses found for the current user");
        }
        return courses.stream()
                .map(course -> {
                    List<CourseModule> modules = moduleRepository.findByCourseOrderByOrderIndexAsc(course);
                    return mapToCourseResponse(course, modules, currentUser);
                })
                .collect(Collectors.toList());
    }




    @Transactional
    public void softDeleteCourse(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        if (currentUser.getRole() != Role.LECTURER || !java.util.Objects.equals(course.getCreator().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Only the creator lecturer can delete this course");
        }
        course.setActive(false);
        courseRepository.save(course);
    }

    public Page<CourseHomeResponse> searchCoursesSummary(String keyword, Long topicId, Pageable pageable) {
        // Hỗ trợ cả người dùng đã đăng nhập và chưa đăng nhập
        User currentUser = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                currentUser = userRepository.findByUsername(username).orElse(null);
            }
        } catch (Exception e) {
            log.debug("No authenticated user found, loading courses for guest");
        }

        // Biến final để sử dụng trong lambda
        final User finalCurrentUser = currentUser;

        Page<Course> courses = courseRepository.searchCourses(keyword, topicId, pageable);
        return courses.map(course -> {
            boolean isEnrolled = false;
            if (finalCurrentUser != null) {
                isEnrolled = enrollmentRepository.existsByUserAndCourse(finalCurrentUser, course);
            }
            return mapToCourseHomeResponse(course, isEnrolled);
        });
    }

    public List<CourseHomeResponse> getLastestCourses() {
        // Hỗ trợ cả người dùng đã đăng nhập và chưa đăng nhập
        User currentUser = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                currentUser = userRepository.findByUsername(username).orElse(null);
            }
        } catch (Exception e) {
            log.debug("No authenticated user found, loading courses for guest");
        }

        // Biến final để sử dụng trong lambda
        final User finalCurrentUser = currentUser;

        // Lấy 6 khóa học mới nhất
        List<Course> courses = courseRepository.findTop6ByActiveTrueOrderByCreatedAtDesc();


        return courses.stream()
                .map(course -> {
                    boolean isEnrolled = false;
                    if (finalCurrentUser != null) {
                        isEnrolled = enrollmentRepository.existsByUserAndCourse(finalCurrentUser, course);
                    }

                    // Đếm số lượng học viên đã đăng ký
                    long enrolledCount = enrollmentRepository.countByCourse(course);

                    return CourseHomeResponse.builder()
                            .id(course.getId())
                            .title(course.getTitle())
                            .summary(course.getDescription())
                            .coverImage(course.getCoverImage())
                            .createdAt(course.getCreatedAt())
                            .topicName(course.getTopic() != null ? course.getTopic().getName() : "Chưa phân loại")
                            .creatorName(course.getCreator() != null ? course.getCreator().getFullname() : "Unknown")
                            .duration(course.getDuration() != null ? course.getDuration() : 0)
                            .price(course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO)
                            .enrolledCount(enrolledCount)
                            .isEnrolled(isEnrolled)
                            .build();
                })
                .collect(Collectors.toList());
    }


    public CourseDetailPublicResponse getCoursePublicDetail(Long id) {
        // Hỗ trợ cả người dùng đã đăng nhập và chưa đăng nhập
        User currentUser = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                currentUser = userRepository.findByUsername(username).orElse(null);
            }
        } catch (Exception e) {
            log.debug("No authenticated user found, loading course detail for guest");
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        EnrollmentStatus enrollmentStatus = EnrollmentStatus.NOT_ENROLLED;

        if (currentUser != null) {
            Optional<CourseEnrollment> enrollmentOpt =
                    enrollmentRepository.findByUserAndCourse(currentUser, course);
            if (enrollmentOpt.isPresent()) {
                enrollmentStatus = enrollmentOpt.get().getStatus();
            }
        }

        List<CourseModule> modules = moduleRepository.findByCourseOrderByOrderIndexAsc(course);
        long enrollmentCount = enrollmentRepository.countByCourse(course);

        return CourseDetailPublicResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .topicName(course.getTopic().getName())
                .content(course.getContent())
                .coverImage(course.getCoverImage())
                .duration(course.getDuration())
                .price(course.getPrice())
                .createdBy(course.getCreator() != null ? course.getCreator().getFullname() : "Unknown")
                .videoCount(modules.stream().mapToInt(m -> m.getVideos().size()).sum())
                .totalEnrolled(enrollmentCount)
                .status(enrollmentStatus)
                .modules(modules.stream()
                        .map(m -> CourseDetailPublicResponse.ModuleInfo.builder()
                                .id(m.getId())
                                .title(m.getTitle())
                                .videos(m.getVideos().stream()
                                        .map(CourseVideo::getTitle)
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public CourseResponse getCourseById(Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username).orElse(null);

        Course course = courseRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        List<CourseModule> modules = moduleRepository.findByCourseOrderByOrderIndexAsc(course);

        boolean isEnrolled = currentUser != null && enrollmentRepository.existsByUserAndCourse(currentUser, course);
        boolean isOwner = currentUser != null && course.getCreator() != null &&
                          course.getCreator().getId() == currentUser.getId();

        if (!isEnrolled && !isOwner) {
            throw new AccessDeniedException("You are not authorized to view this course.");
        }

        return mapToCourseResponse(course, modules, currentUser);
    }

    public List<CourseResponse> getCoursesByLecturer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (currentUser.getRole() != Role.LECTURER) {
            throw new AccessDeniedException("Only lecturer can view their courses");
        }
        List<Course> courses = courseRepository.findByCreatorAndActiveTrue(currentUser);
        List<CourseResponse> responses = new ArrayList<>();
        for (Course course : courses) {
            List<CourseModule> modules = moduleRepository.findByCourse_ActiveTrueOrderByOrderIndexAsc()
                .stream().filter(m -> m.getCourse().getId().equals(course.getId())).toList();
            responses.add(mapToCourseResponse(course, modules, currentUser));
        }
        return responses;
    }


    public void deleteCourse(Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findByIdAndActiveTrue(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));


        if (course.getCreator().getId() != currentUser.getId()) {
            throw new AccessDeniedException("Bạn chỉ có thể xóa các khóa học của chính mình");
        }

        // Set course as inactive instead of deleting
        course.setActive(false);
        courseRepository.save(course);
    }


    private CourseResponse mapToCourseResponse(Course course, List<CourseModule> modules, User currentUser) {
        List<CourseModuleResponse> moduleResponses = modules.stream()
                .map(m -> mapToModuleResponse(m, currentUser))
                .collect(Collectors.toList());

        EnrollmentStatus enrollmentStatus = EnrollmentStatus.NOT_ENROLLED;
        if (currentUser != null) {
            Optional<CourseEnrollment> enrollmentOpt = enrollmentRepository.findByUserAndCourse(currentUser, course);
            if (enrollmentOpt.isPresent()) {
                enrollmentStatus = enrollmentOpt.get().getStatus();
            }
        }


        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .topicName(course.getTopic() != null ? course.getTopic().getName() : null)
                .description(course.getDescription())
                .content(course.getContent())
                .duration(course.getDuration())
                .coverImage(course.getCoverImage())
                .price(course.getPrice())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .creator(course.getCreator().getFullname())
                .modules(moduleResponses)
                .enrollmentCount(course.getEnrolledCount())
                .enrollmentStatus(enrollmentStatus)
                .build();
    }

    private CourseModuleResponse mapToModuleResponse(CourseModule module, User currentUser) {
        List<VideoCourseResponse> videoDTOs = module.getVideos().stream()
                .map(video -> {
                    boolean watched = watchedVideoRepository.existsByUserAndVideoAndWatchedTrue(currentUser, video);
                    return VideoCourseResponse.builder()
                            .id(video.getId())
                            .title(video.getTitle())
                            .videoUrl(video.getVideoUrl())
                            .watched(watched)
                            .build();
                })
                .collect(Collectors.toList());
        return CourseModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .videos(videoDTOs)
                .orderIndex(module.getOrderIndex())
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();
    }

    private CourseHomeResponse mapToCourseHomeResponse(Course course, boolean isEnrolled) {
        // Đếm số lượng học viên đã đăng ký
        long enrolledCount = enrollmentRepository.countByCourse(course);

        CourseHomeResponse dto = new CourseHomeResponse();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setSummary(course.getDescription());
        dto.setCoverImage(course.getCoverImage());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setTopicName(course.getTopic() != null ? course.getTopic().getName() : "Chưa phân loại");
        dto.setCreatorName(course.getCreator() != null ? course.getCreator().getFullname() : "Unknown");
        dto.setDuration(course.getDuration() != null ? course.getDuration() : 0);
        dto.setPrice(course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO);
        dto.setEnrolledCount(enrolledCount);
        dto.setEnrolled(isEnrolled);
        return dto;
    }
    
    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(course -> {
                    List<CourseModule> modules = moduleRepository.findByCourseOrderByOrderIndexAsc(course);
                    return mapToCourseResponse(course, modules, course.getCreator());
                })
                .collect(Collectors.toList());
    }

}
