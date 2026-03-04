package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.response.CertificateResponse;
import com.jungle.courseshop.entity.Certificate;
import com.jungle.courseshop.repository.CertificateRepo;
import com.jungle.courseshop.repository.UserRepo;
import com.jungle.courseshop.service.CertificateService;
import com.jungle.courseshop.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/certificate")
@RequiredArgsConstructor
@Slf4j
public class CertificateController {

    private final CertificateRepo certificateRepo;
    private final CertificateService certificateService;
    private final UserRepo userRepo;

    /**
     * Danh sách tất cả chứng chỉ của user hiện tại
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public String myCertificates(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepo.findByUsernameAndEnabledTrue(username).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Certificate> certs = certificateRepo.findByUserIdOrderByIssuedDateDesc(currentUser.getId());

        List<CertificateResponse> responses = certs.stream().map(cert -> CertificateResponse.builder()
                .id(cert.getId())
                .courseTitle(cert.getCourse().getTitle())
                .username(cert.getUser().getFullname())
                .lecturerName(cert.getCourse().getCreator() != null
                        ? cert.getCourse().getCreator().getFullname() : "Unknown")
                .certificateUrl(cert.getCertificateUrl())
                .courseCoverImage(cert.getCourse().getCoverImage())
                .issuedDate(cert.getIssuedDate())
                .courseId(cert.getCourse().getId())
                .build()
        ).collect(Collectors.toList());

        model.addAttribute("certificates", responses);
        model.addAttribute("title", "Chứng chỉ của tôi");
        return "certificate/my-certificates";
    }

    /**
     * Trang public hiển thị chứng chỉ - ai cũng có thể xem bằng link
     */
    @GetMapping("/{certificateId}")
    public String viewCertificate(@PathVariable Long certificateId, Model model) {
        Certificate certificate = certificateRepo.findById(certificateId).orElse(null);
        if (certificate == null) {
            model.addAttribute("error", "Chứng chỉ không tồn tại");
            return "certificate/view";
        }

        model.addAttribute("certificate", certificate);
        model.addAttribute("studentName", certificate.getUser().getFullname());
        model.addAttribute("courseName", certificate.getCourse().getTitle());
        model.addAttribute("lecturerName", certificate.getCourse().getCreator() != null
                ? certificate.getCourse().getCreator().getFullname() : "Unknown");
        model.addAttribute("issuedDate",
                certificate.getIssuedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("certificateId", certificateId);
        model.addAttribute("certificateUrl", certificate.getCertificateUrl());
        model.addAttribute("title", "Chứng chỉ - " + certificate.getCourse().getTitle());

        return "certificate/view";
    }

    /**
     * Download PDF chứng chỉ - public
     */
    @GetMapping("/{certificateId}/download")
    public ResponseEntity<byte[]> downloadCertificatePdf(@PathVariable Long certificateId) {
        try {
            Certificate certificate = certificateRepo.findById(certificateId)
                    .orElseThrow(() -> new RuntimeException("Chứng chỉ không tồn tại"));

            byte[] pdfBytes = certificateService.generateCertificatePdf(
                    certificate.getUser().getFullname(),
                    certificate.getCourse().getTitle()
            );

            String filename = "certificate_" + certificate.getCourse().getTitle()
                    .replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (RuntimeException e) {
            log.error("Certificate not found: {}", certificateId, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error generating certificate PDF: {}", certificateId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
