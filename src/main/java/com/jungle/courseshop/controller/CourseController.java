package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.CourseCreateRequest;
import com.jungle.courseshop.dto.request.CourseUpdateRequest;
import com.jungle.courseshop.dto.response.CourseDetailPublicResponse;
import com.jungle.courseshop.dto.response.CourseEnrollmentResponse;
import com.jungle.courseshop.dto.response.CourseHomeResponse;
import com.jungle.courseshop.dto.response.CourseResponse;
import com.jungle.courseshop.entity.Certificate;
import com.jungle.courseshop.entity.CourseEnrollment;
import com.jungle.courseshop.entity.EnrollmentStatus;
import com.jungle.courseshop.entity.Topic;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.CertificateRepo;
import com.jungle.courseshop.repository.CourseEnrollmentRepo;
import com.jungle.courseshop.repository.UserRepo;
import com.jungle.courseshop.service.CertificateService;
import com.jungle.courseshop.service.impl.CourseEnrollmentServiceImpl;
import com.jungle.courseshop.service.impl.CourseServiceImpl;
import com.jungle.courseshop.service.impl.TopicServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses")
@Slf4j
public class CourseController {

    private final CourseEnrollmentServiceImpl enrollmentService;
    private final CourseServiceImpl courseService;
    private final TopicServiceImpl topicService;
    private final CertificateService certificateService;
    private final UserRepo userRepo;
    private final CourseEnrollmentRepo enrollmentRepo;
    private final CertificateRepo certificateRepo;

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
    public String learnCourse(@PathVariable Long id, Model model) {
        try {
            CourseResponse course = courseService.getCourseById(id);
            model.addAttribute("course", course);
            model.addAttribute("title", course.getTitle() + " - Học khóa học");

            // Kiểm tra tiến độ và chứng chỉ
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userRepo.findByUsernameAndEnabledTrue(username).orElse(null);
            if (currentUser != null) {
                var enrollments = enrollmentRepo.findByUserOrderByEnrollmentDateDesc(currentUser);
                for (var enrollment : enrollments) {
                    if (enrollment.getCourse().getId().equals(id)) {
                        model.addAttribute("progress", enrollment.getProgress() != null ? enrollment.getProgress() : 0.0);
                        model.addAttribute("isCompleted", enrollment.getStatus() == EnrollmentStatus.COMPLETED);
                        boolean hasCertificate = certificateRepo.findByUserIdAndCourseId(currentUser.getId(), id).isPresent();
                        model.addAttribute("hasCertificate", hasCertificate);
                        break;
                    }
                }
            }

            return "courses/learn";
        } catch (Exception e) {
            log.error("Error loading course for learning", e);
            model.addAttribute("error", "Không thể tải khóa học. Bạn cần đăng ký khóa học này trước.");
            return "redirect:/courses/" + id;
        }
    }

    @GetMapping("/certificate/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public String viewMyCertificate(@PathVariable Long courseId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userRepo.findByUsernameAndEnabledTrue(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Certificate certificate = certificateRepo.findByUserIdAndCourseId(currentUser.getId(), courseId)
                    .orElseThrow(() -> new RuntimeException("Bạn chưa hoàn thành khóa học này"));

            return "redirect:/certificate/" + certificate.getId();
        } catch (RuntimeException e) {
            log.error("Error viewing certificate for course {}", courseId, e);
            return "redirect:/courses/learn/" + courseId + "?error=certificate_not_found";
        }
    }

    @GetMapping("/certificate/{courseId}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long courseId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userRepo.findByUsernameAndEnabledTrue(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Kiểm tra chứng chỉ tồn tại
            Certificate certificate = certificateRepo.findByUserIdAndCourseId(currentUser.getId(), courseId)
                    .orElseThrow(() -> new RuntimeException("Bạn chưa hoàn thành khóa học này"));

            // Tạo PDF chứng chỉ
            byte[] pdfBytes = certificateService.generateCertificatePdf(
                    currentUser.getFullname(),
                    certificate.getCourse().getTitle()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate_" + courseId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (RuntimeException e) {
            log.error("Error downloading certificate for course {}", courseId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error generating certificate PDF for course {}", courseId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/lecture/manage")
    @PreAuthorize("hasRole('LECTURER')")
    public String manageCoursesPage(Model model) {
        try {
            List<CourseResponse> courses = courseService.getCoursesByLecturer();
            model.addAttribute("courses", courses);
            model.addAttribute("title", "Quản lý khóa học của giảng viên");
        } catch (Exception e) {
            log.error("Error loading lecturer courses", e);
            model.addAttribute("error", "Không thể tải danh sách khóa học");
        }
        return "lecturer/courses";
    }

    @GetMapping("/lecture/create")
    @PreAuthorize("hasRole('LECTURER')")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new CourseCreateRequest());
        model.addAttribute("topics", topicService.getAll());
        model.addAttribute("title", "Tạo khóa học mới");
        return "lecturer/create-course";
    }

    @PostMapping("/lecture/create")
    @PreAuthorize("hasRole('LECTURER')")
    public String createCourseSubmit(@ModelAttribute CourseCreateRequest request, Model model) {
        try {
            courseService.createCourse(request);
            return "redirect:/courses/lecture/manage";
        } catch (IOException e) {
            log.error("IO error creating course", e);
            model.addAttribute("error", "Lỗi khi upload ảnh hoặc xử lý dữ liệu");
        } catch (Exception e) {
            log.error("Error creating course", e);
            model.addAttribute("error", "Không thể tạo khóa học");
        }
        model.addAttribute("topics", topicService.getAll());
        model.addAttribute("course", request);
        return "lecturer/create-course";
    }

    @GetMapping("/lecture/{id}/edit")
    @PreAuthorize("hasRole('LECTURER')")
    public String editCourseForm(@PathVariable Long id, Model model) {
        try {
            CourseResponse course = courseService.getCourseById(id);
            model.addAttribute("course", course);
            model.addAttribute("courseId", id); // Add courseId to model
            model.addAttribute("existingCourse", course);
            model.addAttribute("topics", topicService.getAll());
            model.addAttribute("title", "Chỉnh sửa khóa học");
            return "lecturer/edit-course";
        } catch (Exception e) {
            log.error("Error loading course for edit", e);
            model.addAttribute("error", "Không thể tải thông tin khóa học");
            return "redirect:/courses/lecture/manage";
        }
    }

    @PostMapping("/lecture/{id}/edit")
    @PreAuthorize("hasRole('LECTURER')")
    public String editCourseSubmit(@PathVariable Long id, @ModelAttribute CourseUpdateRequest request, Model model) {
        try {
            courseService.updateCourse(id, request);
            return "redirect:/courses/lecture/manage";
        } catch (IOException e) {
            log.error("IO error updating course", e);
            model.addAttribute("error", "Lỗi khi upload ảnh hoặc xử lý dữ liệu");
        } catch (Exception e) {
            log.error("Error updating course", e);
            model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Không thể cập nhật khóa học");
        }

        List<Topic> topics = topicService.getAll();
        model.addAttribute("topics", topics);
        model.addAttribute("courseId", id);

        try {
            CourseResponse course = courseService.getCourseById(id);
            if (request.getTitle() != null)
                course.setTitle(request.getTitle());
            if (request.getDescription() != null)
                course.setDescription(request.getDescription());
            if (request.getContent() != null)
                course.setContent(request.getContent());
            if (request.getPrice() != null)
                course.setPrice(request.getPrice());
            if (request.getDuration() != null)
                course.setDuration(request.getDuration());

            if (request.getTopicId() != null) {
                topics.stream()
                        .filter(t -> t.getId().equals(request.getTopicId()))
                        .findFirst()
                        .ifPresent(t -> course.setTopicName(t.getName()));
            }

            model.addAttribute("course", course);
            model.addAttribute("existingCourse", course);
        } catch (Exception ex) {
            log.error("Could not load course back for error view", ex);
        }

        return "lecturer/edit-course";
    }

    @PostMapping("/lecture/{id}/delete")
    @PreAuthorize("hasRole('LECTURER')")
    public String deleteCourse(@PathVariable Long id, Model model) {
        try {
            courseService.softDeleteCourse(id);
        } catch (Exception e) {
            log.error("Error deleting course", e);
            model.addAttribute("error", "Không thể xóa khóa học");
            return "redirect:/courses/lecture/manage";
        }
        return "redirect:/courses/lecture/manage";
    }

}
