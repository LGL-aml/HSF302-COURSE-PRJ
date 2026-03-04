package com.jungle.courseshop.service.impl;


import com.jungle.courseshop.dto.response.*;
import com.jungle.courseshop.entity.*;
import com.jungle.courseshop.repository.*;
import com.jungle.courseshop.service.CourseEnrollmentService;
import com.jungle.courseshop.service.EmailService;
import com.jungle.courseshop.service.CertificateService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {

    private final CourseRepo courseRepository;
    private final CourseModuleRepo moduleRepository;
    private final CourseEnrollmentRepo enrollmentRepository;
    private final UserRepo userRepository;
    private final EmailService emailService;
    private final WatchedVideoRepo watchedVideoRepository;
    private final CourseVideoRepo videoCourseRepository;
    private final CertificateRepo certificateRepository;
    private final QuizAttemptRepo quizAttemptRepo;
    private final QuizRepo quizRepo;
    private final CertificateService certificateService;

    @Transactional
    public CourseEnrollmentResponse enrollCourse(Long courseId) throws MessagingException, UnsupportedEncodingException, MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));


        // Check if user is already enrolled
        if (enrollmentRepository.existsByUserAndCourse(currentUser, course)) {
            throw new RuntimeException("User already enrolled in this course");
        }

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setUser(currentUser);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setProgress(0.0);

        long enrollmentCount = enrollmentRepository.countByCourse(course);
        course.setEnrolledCount(enrollmentCount);
        courseRepository.save(course);

        CourseEnrollment savedEnrollment = enrollmentRepository.save(enrollment);

        emailService.sendEnrollmentSuccessEmail(
                currentUser.getEmail(),
                currentUser.getFullname(),
                course.getTitle(),
                course.getDuration(),
                course.getCreator().getFullname(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

        log.info("User {} enrolled in course: {}", currentUser.getUsername(), course.getTitle());

        return mapToEnrollmentResponse(savedEnrollment);
    }

    @Transactional
    public CourseEnrollment enrollUserToCourse(User user, Course course) throws MessagingException, UnsupportedEncodingException {
        // Nếu đã enroll thì bỏ qua, tránh lỗi duplicate
        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            return null;
        }

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setProgress(0.0);

        CourseEnrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // cập nhật số lượng học viên
        long enrollmentCount = enrollmentRepository.countByCourse(course);
        course.setEnrolledCount(enrollmentCount);
        courseRepository.save(course);

//        emailService.sendEnrollmentSuccessEmail(
//                user.getEmail(),
//                user.getFullname(),
//                course.getTitle(),
//                course.getDuration(),
//                course.getCreator().getFullname(),
//                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
//        );

        return savedEnrollment;
    }

    public List<CourseEnrollmentResponse> getEnrolledCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        List<CourseEnrollment> enrollments = enrollmentRepository.findByUserOrderByEnrollmentDateDesc(currentUser);

        return enrollments.stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }


    //check watched videos
    public void markVideoAsWatched(Long videoId, boolean watchedStatus) throws MessagingException, UnsupportedEncodingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CourseVideo video = videoCourseRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        Optional<WatchedVideo> optionalWatched = watchedVideoRepository.findByUserAndVideo(user, video);

        if (watchedStatus) {
            // Nếu chưa có thì tạo mới
            WatchedVideo watchedVideo = optionalWatched.orElseGet(() -> {
                WatchedVideo w = new WatchedVideo();
                w.setUser(user);
                w.setVideo(video);
                return w;
            });
            watchedVideo.setWatched(true);
            watchedVideoRepository.save(watchedVideo);
        } else {
            optionalWatched.ifPresent(w -> {
                w.setWatched(false);
                watchedVideoRepository.save(w);
            });
        }

        // Tính progress (video + quiz)
        Course course = video.getCourseModule().getCourse();
        CourseEnrollment enrollment = enrollmentRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        if (enrollment.getProgress() >= 100.0 && enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            // Nếu đã hoàn thành, không cập nhật progress lại nữa
            return;
        }

        updateCourseProgress(user, course, enrollment);
    }

    /**
     * Cập nhật lại progress sau khi làm quiz (gọi từ QuizService)
     */
    @Transactional
    public void recalculateProgress(Long courseId) throws MessagingException, UnsupportedEncodingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseEnrollment enrollment = enrollmentRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            return; // Đã hoàn thành rồi
        }

        updateCourseProgress(user, course, enrollment);
    }

    /**
     * Tính progress = (video đã xem + quiz đã pass) / (tổng video + tổng quiz) * 100
     * Hoàn thành khóa học khi: xem hết video VÀ pass hết quiz
     */
    private void updateCourseProgress(User user, Course course, CourseEnrollment enrollment) throws MessagingException, UnsupportedEncodingException {
        long totalVideos = videoCourseRepository.countByCourseModule_Course(course);
        long watchedVideos = watchedVideoRepository.countByUserAndVideo_CourseModule_Course_AndWatchedTrue(user, course);

        List<CourseModule> modules = moduleRepository.findByCourseOrderByOrderIndexAsc(course);
        long totalQuizzes = 0;
        long passedQuizzes = 0;
        for (CourseModule module : modules) {
            Optional<Quiz> quizOpt = quizRepo.findByCourseModule(module);
            if (quizOpt.isPresent() && quizOpt.get().getActive()) {
                totalQuizzes++;
                if (quizAttemptRepo.existsByUserAndQuizAndPassedTrue(user, quizOpt.get())) {
                    passedQuizzes++;
                }
            }
        }

        long totalItems = totalVideos + totalQuizzes;
        long completedItems = watchedVideos + passedQuizzes;

        if (totalItems == 0) {
            enrollment.setProgress(0.0);
            enrollmentRepository.save(enrollment);
            return;
        }

        double progress = (completedItems * 100.0) / totalItems;
        enrollment.setProgress(Math.min(progress, 100.0));

        boolean allVideosWatched = totalVideos == 0 || watchedVideos >= totalVideos;
        boolean allQuizzesPassed = totalQuizzes == 0 || passedQuizzes >= totalQuizzes;

        if (allVideosWatched && allQuizzesPassed && progress >= 100.0) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletionDate(LocalDateTime.now());
            emailService.sendCourseCompletionEmail(user.getEmail(),
                    user.getFullname(),
                    course.getTitle(),
                    course.getDuration(),
                    course.getCreator().getFullname(),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // Tạo chứng chỉ nếu chưa có
            if (!certificateRepository.existsByUserAndCourse(user, course)) {
                Certificate certificate = new Certificate();
                certificate.setUser(user);
                certificate.setCourse(course);
                certificate.setIssuedDate(LocalDateTime.now());
                Certificate savedCert = certificateRepository.save(certificate);

                // Upload ảnh chứng chỉ lên Cloudinary (async - không block nếu lỗi)
                try {
                    String certImageUrl = certificateService.generateAndUploadCertificateImage(
                            user.getFullname(),
                            course.getTitle(),
                            savedCert.getId()
                    );
                    savedCert.setCertificateUrl(certImageUrl);
                    certificateRepository.save(savedCert);
                    log.info("Certificate image uploaded for user={} course={}: {}", user.getUsername(), course.getId(), certImageUrl);
                } catch (Exception e) {
                    log.error("Failed to upload certificate image to Cloudinary for cert id={}", savedCert.getId(), e);
                }
            }
        }

        enrollmentRepository.save(enrollment);
    }


    public CertificateResponse getCertificateResponse(Long courseId, Long userId) {
        Certificate cert = certificateRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        return CertificateResponse.builder()
                .courseTitle(cert.getCourse().getTitle())
                .username(cert.getUser().getFullname())
                .build();
    }

    private CourseEnrollmentResponse mapToEnrollmentResponse(CourseEnrollment enrollment) {
        return CourseEnrollmentResponse.builder()
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .username(enrollment.getUser().getUsername())
                .coverImage(enrollment.getCourse().getCoverImage())
                .creatorName(enrollment.getCourse().getCreator() != null ? enrollment.getCourse().getCreator().getFullname() : null)
                .description(enrollment.getCourse().getDescription())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .completionDate(enrollment.getCompletionDate())
                .status(enrollment.getStatus())
                .progress(enrollment.getProgress())
                .build();
    }

}
