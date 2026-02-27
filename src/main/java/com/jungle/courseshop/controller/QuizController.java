package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.QuizSubmitRequest;
import com.jungle.courseshop.dto.response.QuizResponse;
import com.jungle.courseshop.dto.response.QuizResultResponse;
import com.jungle.courseshop.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;

    /**
     * Lấy quiz cho sinh viên làm bài (ẩn đáp án đúng)
     */
    @GetMapping("/module/{moduleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizResponse> getQuizForStudent(@PathVariable Long moduleId) {
        try {
            QuizResponse quiz = quizService.getQuizForStudent(moduleId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            log.error("Error getting quiz for module {}: {}", moduleId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy quiz cho giảng viên (hiện đáp án đúng)
     */
    @GetMapping("/module/{moduleId}/answers")
    @PreAuthorize("hasAuthority('LECTURER')")
    public ResponseEntity<QuizResponse> getQuizForLecturer(@PathVariable Long moduleId) {
        try {
            QuizResponse quiz = quizService.getQuizForLecturer(moduleId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            log.error("Error getting quiz answers for module {}: {}", moduleId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Nộp bài quiz
     */
    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizResultResponse> submitQuiz(@RequestBody QuizSubmitRequest request) {
        try {
            QuizResultResponse result = quizService.submitQuiz(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error submitting quiz: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Kiểm tra đã pass quiz chưa
     */
    @GetMapping("/module/{moduleId}/passed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> hasPassedQuiz(@PathVariable Long moduleId) {
        try {
            boolean passed = quizService.hasPassedQuiz(moduleId);
            return ResponseEntity.ok(Map.of("passed", passed));
        } catch (Exception e) {
            log.error("Error checking quiz pass status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
