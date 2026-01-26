package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.response.CourseHomeResponse;
import com.jungle.courseshop.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final CourseService courseService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            // Lấy danh sách 6 khóa học mới nhất
            List<CourseHomeResponse> latestCourses = courseService.getLastestCourses();
            model.addAttribute("latestCourses", latestCourses);

            // Thêm thông tin thống kê (optional - bạn có thể thêm service cho stats sau)
            // Tạm thời comment lại nếu chưa có service
            // Map<String, Long> stats = new HashMap<>();
            // stats.put("totalCourses", courseRepository.count());
            // stats.put("totalStudents", userRepository.countByRole(Role.STUDENT));
            // stats.put("totalLecturers", userRepository.countByRole(Role.LECTURER));
            // model.addAttribute("stats", stats);

            model.addAttribute("title", "Trang chủ - Course Shop");

            log.info("Loaded {} latest courses for home page", latestCourses.size());
        } catch (Exception e) {
            log.error("Error loading home page data", e);
            model.addAttribute("error", "Không thể tải dữ liệu trang chủ");
        }

        return "index";
    }
}

