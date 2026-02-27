package com.jungle.courseshop.controller;

import com.jungle.courseshop.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final CourseEnrollmentService enrollmentService;

    @PostMapping("/{videoId}/watched")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> markVideoWatched(@PathVariable Long videoId) {
        try {
            enrollmentService.markVideoAsWatched(videoId, true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Video đã được đánh dấu hoàn thành"));
        } catch (Exception e) {
            log.error("Error marking video {} as watched", videoId, e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
