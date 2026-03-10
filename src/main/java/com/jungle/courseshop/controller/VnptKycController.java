package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.LecturerRegistrationRequest;
import com.jungle.courseshop.dto.response.UserCardResponse;
import com.jungle.courseshop.dto.response.VnptFaceCompareResponse;
import com.jungle.courseshop.service.UserService;
import com.jungle.courseshop.service.VnptKycService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // Bước 1: Hiển thị trang upload CCCD
    @GetMapping("/upload")
    public String showUploadPage() {
        return "lecturer/kyc-upload";
    }

    // Bước 2: Xử lý upload CCCD → lấy hash → chuyển sang trang xác thực khuôn mặt
    @PostMapping("/upload-cccd")
    public String uploadCccd(@RequestParam("frontImage") MultipartFile frontImage,
                             @RequestParam("backImage") MultipartFile backImage,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            log.info("=== Bước 1: Upload CCCD lên VNPT ===");

            // Upload ảnh CCCD lên VNPT để lấy hash
            String frontHash = vnptKycService.uploadFileToVnpt(frontImage);
            log.info("Upload mặt trước thành công, hash: {}", frontHash);

            String backHash = vnptKycService.uploadFileToVnpt(backImage);
            log.info("Upload mặt sau thành công, hash: {}", backHash);

            // Lưu hash vào session để dùng ở bước tiếp theo
            session.setAttribute("frontHash", frontHash);
            session.setAttribute("backHash", backHash);

            return "redirect:/lecturer/kyc/face-verify";

        } catch (IllegalArgumentException e) {
            log.error("Lỗi khi upload CCCD: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/lecturer/kyc/upload";
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi upload CCCD: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Hệ thống đang bận, vui lòng thử lại sau.");
            return "redirect:/lecturer/kyc/upload";
        }
    }

    // Bước 3: Hiển thị trang xác thực khuôn mặt
    @GetMapping("/face-verify")
    public String showFaceVerifyPage(HttpSession session, RedirectAttributes redirectAttributes) {
        // Kiểm tra đã upload CCCD chưa
        String frontHash = (String) session.getAttribute("frontHash");
        String backHash = (String) session.getAttribute("backHash");

        if (frontHash == null || backHash == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng upload ảnh CCCD trước.");
            return "redirect:/lecturer/kyc/upload";
        }

        return "lecturer/kyc-face-verify";
    }

    // Bước 4: Xử lý xác thực khuôn mặt → so khớp → bóc tách OCR → hiển thị review
    @PostMapping("/verify-face")
    public String verifyFaceAndExtract(@RequestParam("portraitImageBase64") String portraitImageBase64,
                                       HttpSession session,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            String frontHash = (String) session.getAttribute("frontHash");
            String backHash = (String) session.getAttribute("backHash");

            if (frontHash == null || backHash == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc đã hết hạn. Vui lòng upload lại ảnh CCCD.");
                return "redirect:/lecturer/kyc/upload";
            }

            if (portraitImageBase64 == null || portraitImageBase64.isBlank()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chụp ảnh khuôn mặt trước khi xác thực.");
                return "redirect:/lecturer/kyc/face-verify";
            }

            log.info("=== Bước 2: Xác thực khuôn mặt (webcam base64) ===");

            // 1. So khớp khuôn mặt với ảnh CCCD từ dữ liệu base64 do webcam chụp
            VnptFaceCompareResponse faceResult = vnptKycService.compareFaceFromBase64(portraitImageBase64, frontHash);

            // 2. Kiểm tra kết quả so khớp (ngưỡng tin cậy >= 80%)
            Double probability = faceResult.getObject().getProb();
            if (probability == null || probability < 80.0) {
                log.warn("Xác thực khuôn mặt thất bại. Độ tin cậy: {}%", probability);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Xác thực khuôn mặt không thành công. Khuôn mặt không khớp với ảnh trên CCCD (Độ tin cậy: "
                                + String.format("%.1f", probability != null ? probability : 0) + "%). Vui lòng thử lại.");
                return "redirect:/lecturer/kyc/face-verify";
            }

            log.info("Xác thực khuôn mặt thành công! Độ tin cậy: {}%", probability);

            // 3. Bóc tách thông tin CCCD từ hash đã lưu
            log.info("=== Bước 3: Bóc tách thông tin CCCD ===");
            UserCardResponse cardInfo = vnptKycService.extractIdCardInfoFromHashes(frontHash, backHash);

            // 4. Map sang form đăng ký
            LecturerRegistrationRequest lecturerForm = LecturerRegistrationRequest.builder()
                    .identifyNumber(cardInfo.getIdentifyNumber())
                    .fullName(cardInfo.getFullName())
                    .birthDate(cardInfo.getBirthDate())
                    .gender(cardInfo.getGender())
                    .recentLocation(cardInfo.getRecentLocation())
                    .nationality(cardInfo.getNationality())
                    .build();
            model.addAttribute("lecturerForm", lecturerForm);
            model.addAttribute("faceMatchProbability", String.format("%.1f", probability));

            // Xóa hash khỏi session
            session.removeAttribute("frontHash");
            session.removeAttribute("backHash");

            return "lecturer/kyc-review";

        } catch (IllegalArgumentException e) {
            log.error("Lỗi khi xác thực: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/lecturer/kyc/face-verify";
        } catch (Exception e) {
            log.error("Lỗi hệ thống: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Hệ thống đang bận, vui lòng thử lại sau.");
            return "redirect:/lecturer/kyc/face-verify";
        }
    }

    // Bước 5: Người dùng xác nhận lưu thông tin
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

            // Gọi Service lưu xuống DB
            userService.registerLecturer(lecturerForm);

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

    // Bước 6: Trang thành công
    @GetMapping("/success")
    public String showSuccessPage() {
        return "lecturer/kyc-success";
    }
}
