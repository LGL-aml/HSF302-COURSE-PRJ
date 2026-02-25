package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.response.CourseResponse;
import com.jungle.courseshop.service.CourseService;
import com.jungle.courseshop.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecturer")
@PreAuthorize("hasAuthority('LECTURER')")
@Slf4j
public class LecturerController {

    private final CourseService courseService;
    private final TopicService topicService;

    @GetMapping({"", "/"})
    public String lecturerHome() {
        return "redirect:/lecturer/courses";
    }

    @GetMapping("/courses")
    public String myCourses(Model model) {
        try {
            List<CourseResponse> courses = courseService.getCoursesByLecturer();
            model.addAttribute("courses", courses);
            model.addAttribute("title", "Quản lý khóa học");
            model.addAttribute("pageTitle", "Khóa học của tôi");
            model.addAttribute("currentPage", "courses");
        } catch (Exception e) {
            log.error("Error loading lecturer courses", e);
            model.addAttribute("error", "Không thể tải danh sách khóa học");
            model.addAttribute("currentPage", "courses");
        }
        return "lecturer/courses";
    }

    @GetMapping("/courses/create")
    public String createCoursePage(Model model) {
        model.addAttribute("title", "Tạo khóa học mới");
        model.addAttribute("pageTitle", "Tạo khóa học mới");
        model.addAttribute("currentPage", "create");
        model.addAttribute("topics", topicService.getAll());
        return "lecturer/create-course";
    }

    @GetMapping("/courses/{id}/edit")
    public String editCoursePage(@PathVariable Long id, Model model) {
        try {
            CourseResponse course = courseService.getCourseById(id);
            model.addAttribute("course", course);
            model.addAttribute("title", "Chỉnh sửa khóa học");
            model.addAttribute("pageTitle", "Chỉnh sửa khóa học");
            model.addAttribute("currentPage", "courses");
            model.addAttribute("topics", topicService.getAll());
            return "lecturer/edit-course";
        } catch (Exception e) {
            log.error("Error loading course for edit", e);
            return "redirect:/lecturer/courses";
        }
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.softDeleteCourse(id);
            redirectAttributes.addFlashAttribute("message", "Xóa khóa học thành công");
        } catch (Exception e) {
            log.error("Error deleting course", e);
            redirectAttributes.addFlashAttribute("error", "Không thể xóa khóa học");
        }
        return "redirect:/lecturer/courses";
    }

    @GetMapping("/students")
    public String students() {
        return "redirect:/lecturer/courses";
    }

    @GetMapping("/reviews")
    public String reviews() {
        return "redirect:/lecturer/courses";
    }

    @GetMapping("/earnings")
    public String earnings() {
        return "redirect:/lecturer/courses";
    }

    @GetMapping("/settings")
    public String settings() {
        return "redirect:/lecturer/courses";
    }
}
