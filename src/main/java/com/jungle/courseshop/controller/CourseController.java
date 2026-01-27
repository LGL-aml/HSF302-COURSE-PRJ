package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.response.CourseDetailPublicResponse;
import com.jungle.courseshop.dto.response.CourseEnrollmentResponse;
import com.jungle.courseshop.dto.response.CourseHomeResponse;
import com.jungle.courseshop.entity.Topic;
import com.jungle.courseshop.service.CourseEnrollmentService;
import com.jungle.courseshop.service.CourseService;
import com.jungle.courseshop.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
