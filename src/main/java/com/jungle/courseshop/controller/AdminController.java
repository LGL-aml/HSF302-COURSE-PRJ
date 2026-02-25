package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.TopicRequest;
import com.jungle.courseshop.dto.request.UpdateUserRequest;
import com.jungle.courseshop.dto.response.TopicResponse;
import com.jungle.courseshop.dto.response.UserDetailResponse;
import com.jungle.courseshop.entity.Role;
import com.jungle.courseshop.service.impl.AdminStatsService;
import com.jungle.courseshop.service.impl.TopicServiceImpl;
import com.jungle.courseshop.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final UserServiceImpl userService;
    private final TopicServiceImpl topicService;
    private final AdminStatsService adminStatsService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "period", required = false, defaultValue = "month") String period,
                            Model model) {
        try {
            List<UserDetailResponse> users = userService.getAllUsers();
            model.addAttribute("recentUsers", users.stream().limit(5).toList());
            model.addAttribute("title", "Admin Dashboard");

            var stats = adminStatsService.getDashboardStats(period);
            model.addAttribute("stats", stats);
            model.addAttribute("period", period);

            // Backward-compatible attributes used by existing template cards
            model.addAttribute("totalUsers", stats.getTotalUsers());
            model.addAttribute("totalTopics", stats.getTotalTopics());
            model.addAttribute("totalCourses", stats.getTotalCourses());

        } catch (Exception e) {
            log.error("Error loading admin dashboard", e);
            model.addAttribute("error", "Error loading dashboard data");
        }
        return "admin/dashboard";
    }

    // --- User Management ---

    @GetMapping("/users")
    public String usersList(Model model) {
        try {
            List<UserDetailResponse> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("title", "Quản lý người dùng");
        } catch (Exception e) {
            log.error("Error loading users", e);
            model.addAttribute("error", "Không thể tải danh sách người dùng");
        }
        return "admin/users/list";
    }

    @GetMapping("/users/create")
    public String createUserPage(Model model) {
        model.addAttribute("registerRequest", new com.jungle.courseshop.dto.request.RegisterRequest());
        model.addAttribute("roles", Role.values());
        model.addAttribute("title", "Thêm người dùng mới");
        return "admin/users/create";
    }

    @PostMapping("/users")
    public String createUser(@jakarta.validation.Valid @ModelAttribute("registerRequest") com.jungle.courseshop.dto.request.RegisterRequest request,
                             org.springframework.validation.BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("title", "Thêm người dùng mới");
            return "admin/users/create";
        }
        try {
            userService.createUser(request);
            redirectAttributes.addFlashAttribute("message", "Tạo người dùng thành công");
            return "redirect:/admin/users";
        } catch (Exception e) {
            log.error("Error creating user", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "admin/users/create";
        }
    }

    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        try {
            UserDetailResponse user = userService.getUsersById(id);
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            model.addAttribute("title", "Chi tiết người dùng");
        } catch (Exception e) {
            log.error("Error loading user detail", e);
            return "redirect:/admin/users";
        }
        return "admin/users/detail";
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id, @ModelAttribute UpdateUserRequest request, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, request);
            redirectAttributes.addFlashAttribute("message", "Cập nhật người dùng thành công");
        } catch (Exception e) {
            log.error("Error updating user", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "Đã khóa tài khoản người dùng");
        } catch (Exception e) {
            log.error("Error deleting user", e);
            redirectAttributes.addFlashAttribute("error", "Không thể xóa người dùng");
        }
        return "redirect:/admin/users";
    }

    // --- Topic Management ---

    @GetMapping("/topics")
    public String topicsList(Model model) {
        List<TopicResponse> topics = List.of();
        try {
            topics = topicService.getAllTopics();
        } catch (Exception e) {
            log.error("Error loading topics", e);
            model.addAttribute("error", "Không thể tải danh sách chủ đề: " + e.getMessage());
        }
        model.addAttribute("topics", topics);
        model.addAttribute("newTopic", new TopicRequest());
        model.addAttribute("title", "Quản lý chủ đề");
        return "admin/topics/list";
    }

    @PostMapping("/topics")
    public String createTopic(@jakarta.validation.Valid @ModelAttribute("newTopic") TopicRequest request,
                              org.springframework.validation.BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Lỗi dữ liệu: ");
            bindingResult.getAllErrors().forEach(error -> errorMsg.append(error.getDefaultMessage()).append("; "));
            redirectAttributes.addFlashAttribute("error", errorMsg.toString());
            return "redirect:/admin/topics";
        }
        try {
            topicService.create(request);
            redirectAttributes.addFlashAttribute("message", "Tạo chủ đề thành công");
        } catch (Exception e) {
            log.error("Error creating topic", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/topics";
    }
    
    @PostMapping("/topics/{id}/delete")
    public String deleteTopic(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            topicService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Xóa chủ đề thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/topics";
    }
}
