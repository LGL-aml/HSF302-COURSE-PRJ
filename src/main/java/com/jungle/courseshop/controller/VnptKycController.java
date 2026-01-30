package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.LecturerRegistrationRequest;
import com.jungle.courseshop.dto.response.UserCardResponse;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.UserRepo;
import com.jungle.courseshop.service.UserService;
import com.jungle.courseshop.service.VnptKycService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lecturer/kyc")
@RequiredArgsConstructor
@Slf4j
public class VnptKycController {
    private final VnptKycService vnptKycService;
    private final UserService userService;
    private final UserRepo userRepo;

    // Bước 1: Hiển thị trang upload
    @GetMapping("/upload")
    public String showUploadPage() {
        return "lecturer/kyc-upload";
    }

    // Bước 2: Xử lý upload và bóc tách
    @PostMapping("/extract")
    public String extractData(@RequestParam("frontImage") MultipartFile frontImage,
                              @RequestParam("backImage") MultipartFile backImage,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            // 1. Gọi Service để bóc tách thông tin từ CCCD
            UserCardResponse cardInfo = vnptKycService.extractIdCardInfo(frontImage, backImage);

            // 2. Map UserCardResponse sang LecturerRegistrationRequest với đầy đủ thông tin
            LecturerRegistrationRequest lecturerForm = LecturerRegistrationRequest.builder()
                    .identifyNumber(cardInfo.getIdentifyNumber())
                    .fullName(cardInfo.getFullName())
                    .birthDate(cardInfo.getBirthDate())
                    .gender(cardInfo.getGender())
                    .recentLocation(cardInfo.getRecentLocation())
                    .nationality(cardInfo.getNationality())
                    .build();
            model.addAttribute("lecturerForm", lecturerForm);

            return "lecturer/kyc-review";

        } catch (IllegalArgumentException e) {
            log.error("Lỗi khi bóc tách CCCD: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/lecturer/kyc/upload";
        } catch (Exception e) {
            log.error("Lỗi hệ thống: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Hệ thống đang bận, vui lòng thử lại sau.");
            return "redirect:/lecturer/kyc/upload";
        }
    }

    // Bước 3: Người dùng xác nhận lưu thông tin
    @PostMapping("/confirm")
    public String confirmRegistration(@ModelAttribute LecturerRegistrationRequest lecturerForm,
                                      RedirectAttributes redirectAttributes) {

        log.info("=== Bắt đầu xử lý đăng ký lecturer ===");
        log.info("Identify Number: {}", lecturerForm.getIdentifyNumber());
        log.info("Full Name: {}", lecturerForm.getFullName());

        try {
            // Validate dữ liệu cơ bản
            if (lecturerForm.getPhone() == null || lecturerForm.getEmail() == null || lecturerForm.getBio() == null) {
                throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin bắt buộc");
            }

            // Lấy user hiện tại từ Security Context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userRepo.findByUsernameAndEnabledTrue(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            log.info("User hiện tại: {}", username);

            // Gọi Service lưu xuống DB
            userService.registerLecturer(currentUser, lecturerForm);
            log.info("Đăng ký lecturer thành công cho user: {}", username);

            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký giảng viên thành công!");
            return "redirect:/lecturer/kyc/success";

        } catch (IllegalArgumentException e) {
            log.error("Lỗi validation: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/lecturer/kyc/upload";
        } catch (RuntimeException e) {
            log.error("Lỗi runtime: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/lecturer/kyc/upload";
        } catch (Exception e) {
            log.error("Lỗi hệ thống: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại.");
            return "redirect:/lecturer/kyc/upload";
        }
    }

    // Bước 4: Trang thành công
    @GetMapping("/success")
    public String showSuccessPage() {
        return "lecturer/kyc-success";
    }
}
