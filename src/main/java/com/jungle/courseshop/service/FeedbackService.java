package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.FeedbackResponse;
import com.jungle.courseshop.entity.*;
import com.jungle.courseshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final CourseVideoRepo courseVideoRepo;
    private final UserRepo userRepo;
    private final CourseEnrollmentRepo enrollmentRepo;

    public List<FeedbackResponse> getFeedbackTreeForVideo(Long videoId) {
        return feedbackRepo.findByVideo_IdAndParentIsNullOrderByCreatedAtAsc(videoId)
                .stream()
                .map(this::toResponseTree)
                .toList();
    }

    @Transactional
    public FeedbackResponse createComment(Long videoId, String content) {
        User currentUser = getCurrentUser();
        CourseVideo video = courseVideoRepo.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        Course course = video.getCourseModule().getCourse();
        if (!enrollmentRepo.existsByUserAndCourse(currentUser, course)) {
            throw new AccessDeniedException("Bạn cần đăng ký khóa học để bình luận.");
        }

        Feedback fb = new Feedback();
        fb.setVideo(video);
        fb.setUser(currentUser);
        fb.setContent(requireContent(content));
        Feedback saved = feedbackRepo.save(fb);
        return toResponseTree(saved);
    }

    @Transactional
    public FeedbackResponse reply(Long parentId, String content) {
        User currentUser = getCurrentUser();
        Feedback parent = feedbackRepo.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        CourseVideo video = parent.getVideo();
        Course course = video.getCourseModule().getCourse();

        // Chỉ giảng viên là creator của course mới được reply (hợp lý nhất theo model hiện tại)
        if (currentUser.getRole() != Role.LECTURER || course.getCreator() == null || currentUser.getId() != course.getCreator().getId()) {
            throw new AccessDeniedException("Chỉ giảng viên của khóa học mới có thể trả lời.");
        }

        Feedback fb = new Feedback();
        fb.setVideo(video);
        fb.setUser(currentUser);
        fb.setParent(parent);
        fb.setContent(requireContent(content));
        Feedback saved = feedbackRepo.save(fb);
        return toResponseTree(saved);
    }

    private FeedbackResponse toResponseTree(Feedback fb) {
        String avatar = null;
        try {
            avatar = fb.getUser() != null ? fb.getUser().getAvatar() : null;
        } catch (Exception ignored) {
        }

        return new FeedbackResponse(
                fb.getId(),
                fb.getContent(),
                fb.getUser() != null ? fb.getUser().getId() : null,
                fb.getUser() != null ? fb.getUser().getUsername() : null,
                avatar,
                fb.getCreatedAt(),
                fb.getReplies() == null ? List.of() : fb.getReplies().stream().map(this::toResponseTree).toList()
        );
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            throw new AccessDeniedException("Vui lòng đăng nhập");
        }
        String username = authentication.getName();
        return userRepo.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String requireContent(String content) {
        String c = content == null ? "" : content.trim();
        if (c.isBlank()) throw new RuntimeException("Nội dung không được để trống");
        if (c.length() > 4000) throw new RuntimeException("Nội dung quá dài");
        return c;
    }
}
