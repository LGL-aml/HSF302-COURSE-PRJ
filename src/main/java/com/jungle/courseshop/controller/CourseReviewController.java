package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.response.CourseReviewResponse;
import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.CourseReview;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.CourseEnrollmentRepo;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.repository.CourseReviewRepo;
import com.jungle.courseshop.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class CourseReviewController {

    private final CourseReviewRepo reviewRepo;
    private final CourseRepo courseRepo;
    private final UserRepo userRepo;
    private final CourseEnrollmentRepo enrollmentRepo;

    /**
     * Get all reviews for a course (public)
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseReviews(@PathVariable Long courseId) {
        try {
            List<CourseReview> reviews = reviewRepo.findByCourseIdOrderByCreatedAtDesc(courseId);
            Double avgRating = reviewRepo.findAverageRatingByCourseId(courseId);
            Long totalReviews = reviewRepo.countByCourseId(courseId);
            List<Object[]> distribution = reviewRepo.findRatingDistributionByCourseId(courseId);

            // Build rating distribution map
            Map<Integer, Long> ratingDist = new LinkedHashMap<>();
            for (int i = 5; i >= 1; i--) ratingDist.put(i, 0L);
            for (Object[] row : distribution) {
                ratingDist.put((Integer) row[0], (Long) row[1]);
            }

            List<CourseReviewResponse> reviewResponses = reviews.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("reviews", reviewResponses);
            result.put("avgRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0);
            result.put("totalReviews", totalReviews);
            result.put("distribution", ratingDist);

            // Check if current user can review
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                User user = userRepo.findByUsernameAndEnabledTrue(auth.getName()).orElse(null);
                if (user != null) {
                    boolean isEnrolled = enrollmentRepo.findByUserIdAndCourseId(user.getId(), courseId).isPresent();
                    boolean hasReviewed = reviewRepo.existsByUserIdAndCourseId(user.getId(), courseId);
                    result.put("canReview", isEnrolled && !hasReviewed);
                    result.put("hasReviewed", hasReviewed);

                    if (hasReviewed) {
                        CourseReview existing = reviewRepo.findByUserIdAndCourseId(user.getId(), courseId).orElse(null);
                        if (existing != null) {
                            result.put("userReview", toResponse(existing));
                        }
                    }
                }
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching reviews for course {}", courseId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Submit or update a review (authenticated + enrolled only)
     */
    @PostMapping("/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> submitReview(@PathVariable Long courseId, @RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepo.findByUsernameAndEnabledTrue(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check enrollment
            boolean isEnrolled = enrollmentRepo.findByUserIdAndCourseId(user.getId(), courseId).isPresent();
            if (!isEnrolled) {
                return ResponseEntity.badRequest().body(Map.of("error", "Bạn cần đăng ký khóa học trước khi đánh giá"));
            }

            int rating = ((Number) body.get("rating")).intValue();
            String comment = (String) body.getOrDefault("comment", "");

            if (rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().body(Map.of("error", "Rating phải từ 1-5"));
            }

            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Check if review already exists (update it)
            Optional<CourseReview> existing = reviewRepo.findByUserIdAndCourseId(user.getId(), courseId);
            CourseReview review;
            if (existing.isPresent()) {
                review = existing.get();
                review.setRating(rating);
                review.setComment(comment);
            } else {
                review = new CourseReview();
                review.setUser(user);
                review.setCourse(course);
                review.setRating(rating);
                review.setComment(comment);
            }

            reviewRepo.save(review);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", existing.isPresent() ? "Đã cập nhật đánh giá!" : "Đánh giá thành công!",
                "review", toResponse(review)
            ));
        } catch (Exception e) {
            log.error("Error submitting review for course {}", courseId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi hệ thống"));
        }
    }

    /**
     * Delete user's own review
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepo.findByUsernameAndEnabledTrue(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CourseReview review = reviewRepo.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("Review not found"));

            if (review.getUser().getId() != user.getId()) {
                return ResponseEntity.status(403).body(Map.of("error", "Bạn chỉ có thể xóa đánh giá của mình"));
            }

            reviewRepo.delete(review);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã xóa đánh giá"));
        } catch (Exception e) {
            log.error("Error deleting review {}", reviewId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi hệ thống"));
        }
    }

    /**
     * Get top rated courses for admin dashboard
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Map<String, Object>>> topRatedCourses() {
        try {
            List<Object[]> topRated = reviewRepo.findTopRatedCourses();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object[] row : topRated) {
                if (result.size() >= 10) break;
                Map<String, Object> map = new HashMap<>();
                map.put("courseId", row[0]);
                map.put("title", row[1]);
                map.put("avgRating", Math.round(((Number) row[2]).doubleValue() * 10.0) / 10.0);
                map.put("reviewCount", row[3]);
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching top rated courses", e);
            return ResponseEntity.ok(List.of());
        }
    }

    private CourseReviewResponse toResponse(CourseReview review) {
        return new CourseReviewResponse(
            review.getId(),
            review.getUser().getId(),
            review.getUser().getUsername(),
            review.getUser().getFullname(),
            review.getUser().getAvatar(),
            review.getCourse().getId(),
            review.getRating(),
            review.getComment(),
            review.getCreatedAt()
        );
    }
}
