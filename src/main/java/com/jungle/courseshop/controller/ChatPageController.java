package com.jungle.courseshop.controller;

import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.CourseEnrollment;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.CourseEnrollmentRepo;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatPageController {

    private final CourseRepo courseRepo;
    private final UserRepo userRepo;
    private final CourseEnrollmentRepo enrollmentRepo;

    @GetMapping("/room")
    public String room(@RequestParam Long courseId,
                       @RequestParam(required = false) Long studentId,
                       Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepo.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean isCreator = course.getCreator() != null && course.getCreator().getId() == currentUser.getId();
        boolean isEnrolled = enrollmentRepo.existsByUserAndCourse(currentUser, course);
        if (!isCreator && !isEnrolled) {
            throw new RuntimeException("Bạn không có quyền chat cho khóa học này");
        }

        User recipient;
        if (isCreator) {
            if (studentId == null) {
                // lecturer must choose student first
                return "redirect:/chat/lecturer/students?courseId=" + courseId;
            }
            User student = userRepo.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            if (!enrollmentRepo.existsByUserAndCourse(student, course)) {
                throw new RuntimeException("Học viên chưa đăng ký khóa học này");
            }
            recipient = student;
        } else {
            if (course.getCreator() == null) {
                throw new RuntimeException("Khóa học chưa có giảng viên");
            }
            recipient = course.getCreator();
        }

        model.addAttribute("course", course);
        model.addAttribute("recipient", recipient);

        return "chat/room";
    }

    @GetMapping("/lecturer/students")
    public String lecturerStudents(@RequestParam Long courseId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepo.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean isCreator = course.getCreator() != null && course.getCreator().getId() == currentUser.getId();
        if (!isCreator) {
            throw new RuntimeException("Bạn không có quyền truy cập");
        }

        List<CourseEnrollment> enrollments = enrollmentRepo.findByCourse(course);
        List<User> students = enrollments.stream()
                .map(CourseEnrollment::getUser)
                .collect(Collectors.toList());

        model.addAttribute("course", course);
        model.addAttribute("students", students);
        return "chat/lecturer-students";
    }
}
