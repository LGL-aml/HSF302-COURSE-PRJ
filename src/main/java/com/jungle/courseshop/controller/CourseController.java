package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.response.*;
import com.jungle.courseshop.entity.Topic;
import com.jungle.courseshop.service.CourseEnrollmentService;
import com.jungle.courseshop.service.CourseService;
import com.jungle.courseshop.service.TopicService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses")
@Slf4j
public class CourseController {

    private final CourseEnrollmentService enrollmentService;

    private final CourseService courseService;

    private final TopicService topicService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public String myCoursesPage(Model model) {
        try {
            List<CourseEnrollmentResponse> enrollments = enrollmentService.getEnrolledCourses();
            model.addAttribute("enrollments", enrollments);
            model.addAttribute("title", "Khóa học của tôi");
        } catch (Exception e) {
            log.error("Error loading enrolled courses", e);
            model.addAttribute("error", "Không thể tải danh sách khóa học");
        }
        return "my-courses/index";
    }

    @GetMapping
    public String coursesListPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long topicId,
            @PageableDefault(size = 6, sort = "id") Pageable pageable,
            Model model) {
        try {
            Page<CourseHomeResponse> courses = courseService.searchCoursesSummary(keyword, topicId, pageable);
            List<Topic> topics = topicService.getAll();

            model.addAttribute("courses", courses);
            model.addAttribute("topics", topics);
            model.addAttribute("keyword", keyword);
            model.addAttribute("topicId", topicId);
            model.addAttribute("title", "Danh sách khóa học");
        } catch (Exception e) {
            log.error("Error loading courses list", e);
            model.addAttribute("error", "Không thể tải danh sách khóa học");
        }
        return "courses/list";
    }

    @GetMapping("/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        try {
            CourseDetailPublicResponse course = courseService.getCoursePublicDetail(id);
            model.addAttribute("course", course);
            model.addAttribute("title", course.getTitle() + " - Chi tiết khóa học");
            return "courses/detail";
        } catch (Exception e) {
            log.error("Error loading course detail", e);
            model.addAttribute("error", "Không thể tải thông tin khóa học");
            return "redirect:/courses";
        }
    }

    @GetMapping("/learn/{id}")
    @PreAuthorize("isAuthenticated()")
    public String learnCourse(@PathVariable Long id,
                             @RequestParam(required = false) Long videoId,
                             Model model) {
        try {
            // Lấy thông tin chi tiết khóa học với modules và videos
            CourseResponse course = courseService.getCourseById(id);

            // Tìm video được chọn hoặc lấy video đầu tiên
            VideoCourseResponse selectedVideo = null;
            if (videoId != null) {
                // Tìm video theo ID
                for (CourseModuleResponse module : course.getModules()) {
                    for (VideoCourseResponse video : module.getVideos()) {
                        if (video.getId().equals(videoId)) {
                            selectedVideo = video;
                            break;
                        }
                    }
                    if (selectedVideo != null) break;
                }
            }

            // Nếu không tìm thấy hoặc không có videoId, lấy video đầu tiên
            if (selectedVideo == null && !course.getModules().isEmpty()
                && !course.getModules().get(0).getVideos().isEmpty()) {
                selectedVideo = course.getModules().get(0).getVideos().get(0);
            }

            // Chuyển đổi URL video sang định dạng embed
            if (selectedVideo != null) {
                selectedVideo.setVideoUrl(convertToEmbedUrl(selectedVideo.getVideoUrl()));
            }

            // Chuyển đổi tất cả video URLs sang embed format
            for (CourseModuleResponse module : course.getModules()) {
                for (VideoCourseResponse video : module.getVideos()) {
                    video.setVideoUrl(convertToEmbedUrl(video.getVideoUrl()));
                }
            }

            // Tính toán progress
            long totalVideos = course.getModules().stream()
                    .mapToLong(m -> m.getVideos().size())
                    .sum();
            long watchedVideos = course.getModules().stream()
                    .flatMap(m -> m.getVideos().stream())
                    .filter(VideoCourseResponse::isWatched)
                    .count();
            double progressPercent = totalVideos > 0 ? (watchedVideos * 100.0 / totalVideos) : 0;

            model.addAttribute("course", course);
            model.addAttribute("selectedVideo", selectedVideo);
            model.addAttribute("totalVideos", totalVideos);
            model.addAttribute("watchedVideos", watchedVideos);
            model.addAttribute("progressPercent", progressPercent);
            model.addAttribute("title", course.getTitle() + " - Học tập");
            return "my-courses/detail";
        } catch (Exception e) {
            log.error("Error loading course learning page", e);
            model.addAttribute("error", "Không thể tải khóa học: " + e.getMessage());
            return "redirect:/courses/me";
        }
    }

    /**
     * Convert YouTube, Vimeo URLs to embed format
     */
    private String convertToEmbedUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        // YouTube formats:
        // https://www.youtube.com/watch?v=VIDEO_ID
        // https://youtu.be/VIDEO_ID
        // Convert to: https://www.youtube.com/embed/VIDEO_ID
        if (url.contains("youtube.com/watch")) {
            String videoId = url.substring(url.indexOf("v=") + 2);
            if (videoId.contains("&")) {
                videoId = videoId.substring(0, videoId.indexOf("&"));
            }
            return "https://www.youtube.com/embed/" + videoId;
        } else if (url.contains("youtu.be/")) {
            String videoId = url.substring(url.lastIndexOf("/") + 1);
            if (videoId.contains("?")) {
                videoId = videoId.substring(0, videoId.indexOf("?"));
            }
            return "https://www.youtube.com/embed/" + videoId;
        }
        // Vimeo format:
        // https://vimeo.com/VIDEO_ID
        // Convert to: https://player.vimeo.com/video/VIDEO_ID
        else if (url.contains("vimeo.com/") && !url.contains("player.vimeo.com")) {
            String videoId = url.substring(url.lastIndexOf("/") + 1);
            return "https://player.vimeo.com/video/" + videoId;
        }

        // If already in embed format or other platforms, return as-is
        return url;
    }

    @PostMapping("/videos/{videoId}/mark-watched")
    @PreAuthorize("isAuthenticated()")
    public String markVideoAsWatched(@PathVariable Long videoId,
                                    @RequestParam Long courseId,
                                    @RequestParam(defaultValue = "true") boolean watched,
                                    RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.markVideoAsWatched(videoId, watched);
            redirectAttributes.addFlashAttribute("message", "Video đã được đánh dấu đã xem");
        } catch (Exception e) {
            log.error("Error marking video as watched", e);
            redirectAttributes.addFlashAttribute("error", "Không thể đánh dấu video: " + e.getMessage());
        }
        return "redirect:/courses/learn/" + courseId + "?videoId=" + videoId;
    }

    @PostMapping("/videos/watched/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> markVideoWatched(@PathVariable Long videoId, @RequestParam boolean watched) throws MessagingException, UnsupportedEncodingException {
        enrollmentService.markVideoAsWatched(videoId, watched);
        RestResponse<Void> response = RestResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Video watched status updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }
}
