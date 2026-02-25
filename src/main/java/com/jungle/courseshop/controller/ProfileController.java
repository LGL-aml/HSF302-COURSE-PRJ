package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.UpdateUserRequest;
import com.jungle.courseshop.dto.response.UserDetailResponse;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.UserRepo;
import com.jungle.courseshop.service.UserService;
import com.jungle.courseshop.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model) {

        UserDetailResponse user = userService.getUserByUsername();

        model.addAttribute("user", user);
        model.addAttribute("updateUserRequest", new UpdateUserRequest());
        model.addAttribute("title", "Hồ sơ");
        return "profile/index";
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@ModelAttribute UpdateUserRequest updateUserRequest,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserProfile(updateUserRequest);
            redirectAttributes.addFlashAttribute("message", "Cập nhật hồ sơ thành công");
        } catch (Exception e) {
            log.error("Error updating profile", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}
