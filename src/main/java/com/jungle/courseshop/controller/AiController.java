package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.AiChatRequest;
import com.jungle.courseshop.dto.request.AiQuizGenerateRequest;
import com.jungle.courseshop.dto.response.AiChatResponse;
import com.jungle.courseshop.dto.response.AiQuizResponse;
import com.jungle.courseshop.dto.response.CourseEnrollmentResponse;
import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.CourseModule;
import com.jungle.courseshop.entity.CourseVideo;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.service.CourseEnrollmentService;
import com.jungle.courseshop.service.impl.CerebrasAiService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final CerebrasAiService cerebrasAiService;
    private final CourseEnrollmentService courseEnrollmentService;
    private final CourseRepo courseRepo;

    /**
     * Lấy danh sách khóa học mà user đã enroll
     */
    @GetMapping("/courses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CourseSimpleDto>> getEnrolledCourses() {
        try {
            List<CourseEnrollmentResponse> enrollments = courseEnrollmentService.getEnrolledCourses();
            
            List<CourseSimpleDto> courses = enrollments.stream()
                    .map(e -> new CourseSimpleDto(
                            e.getCourseId(),
                            e.getCourseTitle()
                    ))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("Error getting enrolled courses for AI", e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Lấy danh sách videos của một khóa học
     */
    @GetMapping("/courses/{courseId}/videos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VideoSimpleDto>> getCourseVideos(@PathVariable Long courseId) {
        try {
            Optional<Course> courseOpt = courseRepo.findById(courseId);
            if (courseOpt.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            Course course = courseOpt.get();
            List<VideoSimpleDto> videos = new ArrayList<>();
            
            for (CourseModule module : course.getModules()) {
                for (CourseVideo video : module.getVideos()) {
                    videos.add(new VideoSimpleDto(
                            video.getId(),
                            video.getTitle(),
                            module.getTitle()
                    ));
                }
            }
            
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            log.error("Error getting videos for course " + courseId, e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Chat với AI
     */
    @PostMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        try {
            log.info("AI Chat request - mode: {}, courseId: {}, videoId: {}", 
                    request.getMode(), request.getCourseId(), request.getVideoId());
            
            AiChatResponse response = cerebrasAiService.chat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in AI chat", e);
            return ResponseEntity.ok(new AiChatResponse(
                    "Xin lỗi, AI đang gặp sự cố. Vui lòng thử lại sau. Lỗi: " + e.getMessage()
            ));
        }
    }

    /**
     * Generate quiz từ nội dung bài học
     */
    @PostMapping("/quiz/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiQuizResponse> generateQuiz(@RequestBody AiQuizGenerateRequest request) {
        try {
            log.info("AI Quiz generation request - courseId: {}, videoId: {}, numQuestions: {}", 
                    request.getCourseId(), request.getVideoId(), request.getNumQuestions());
            
            AiQuizResponse response = cerebrasAiService.generateQuiz(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating quiz", e);
            return ResponseEntity.ok(new AiQuizResponse(Collections.emptyList()));
        }
    }

    // DTOs đơn giản cho response
    @Data
    @AllArgsConstructor
    public static class CourseSimpleDto {
        private Long id;
        private String title;
    }

    @Data
    @AllArgsConstructor
    public static class VideoSimpleDto {
        private Long id;
        private String title;
        private String moduleName;
    }
}
