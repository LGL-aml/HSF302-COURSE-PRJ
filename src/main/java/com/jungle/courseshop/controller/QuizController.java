package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.QuizCreateRequest;
import com.jungle.courseshop.dto.request.QuizSubmitRequest;
import com.jungle.courseshop.dto.response.QuizAttemptResponse;
import com.jungle.courseshop.dto.response.QuizDetailResponse;
import com.jungle.courseshop.dto.response.QuizResponse;
import com.jungle.courseshop.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;

    /**
     * Tạo quiz mới (Lecturer/Admin)
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('LECTURER', 'ADMIN')")
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody QuizCreateRequest request) {
        try {
            log.info("Creating new quiz: {}", request.getTitle());
            QuizResponse response = quizService.createQuiz(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating quiz", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cập nhật quiz (Lecturer/Admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'ADMIN')")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long id, 
                                                    @RequestBody QuizCreateRequest request) {
        try {
            log.info("Updating quiz: {}", id);
            QuizResponse response = quizService.updateQuiz(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating quiz", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Xóa quiz (Lecturer/Admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        try {
            log.info("Deleting quiz: {}", id);
            quizService.deleteQuiz(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting quiz", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy thông tin quiz
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable Long id) {
        try {
            QuizResponse response = quizService.getQuizById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting quiz", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy chi tiết quiz với câu hỏi (để làm bài)
     */
    @GetMapping("/{id}/detail")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizDetailResponse> getQuizDetail(@PathVariable Long id) {
        try {
            QuizDetailResponse response = quizService.getQuizDetail(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting quiz detail", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy danh sách quiz của course
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuizResponse>> getQuizzesByCourse(@PathVariable Long courseId) {
        try {
            List<QuizResponse> quizzes = quizService.getQuizzesByCourse(courseId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            log.error("Error getting quizzes by course", e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Lấy danh sách quiz của module
     */
    @GetMapping("/module/{moduleId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuizResponse>> getQuizzesByModule(@PathVariable Long moduleId) {
        try {
            List<QuizResponse> quizzes = quizService.getQuizzesByModule(moduleId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            log.error("Error getting quizzes by module", e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Lấy quiz của giảng viên (Lecturer)
     */
    @GetMapping("/my-quizzes")
    @PreAuthorize("hasAuthority('LECTURER')")
    public ResponseEntity<List<QuizResponse>> getMyQuizzes() {
        try {
            List<QuizResponse> quizzes = quizService.getMyQuizzes();
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            log.error("Error getting my quizzes", e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Bắt đầu làm quiz
     */
    @PostMapping("/{id}/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizAttemptResponse> startQuiz(@PathVariable Long id) {
        try {
            log.info("User starting quiz: {}", id);
            QuizAttemptResponse response = quizService.startQuizAttempt(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting quiz", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Nộp bài quiz
     */
    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizAttemptResponse> submitQuiz(@RequestBody QuizSubmitRequest request) {
        try {
            log.info("User submitting quiz attempt: {}", request.getAttemptId());
            QuizAttemptResponse response = quizService.submitQuiz(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting quiz", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Xem chi tiết một lần làm bài
     */
    @GetMapping("/attempt/{attemptId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizAttemptResponse> getAttemptDetail(@PathVariable Long attemptId) {
        try {
            QuizAttemptResponse response = quizService.getAttemptDetail(attemptId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting attempt detail", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy lịch sử làm bài của user cho quiz
     */
    @GetMapping("/{id}/my-attempts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuizAttemptResponse>> getMyAttempts(@PathVariable Long id) {
        try {
            List<QuizAttemptResponse> attempts = quizService.getMyAttempts(id);
            return ResponseEntity.ok(attempts);
        } catch (Exception e) {
            log.error("Error getting my attempts", e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Lấy tất cả attempts của quiz (Lecturer/Admin)
     */
    @GetMapping("/{id}/all-attempts")
    @PreAuthorize("hasAnyAuthority('LECTURER', 'ADMIN')")
    public ResponseEntity<List<QuizAttemptResponse>> getAllAttempts(@PathVariable Long id) {
        try {
            List<QuizAttemptResponse> attempts = quizService.getAllAttemptsForQuiz(id);
            return ResponseEntity.ok(attempts);
        } catch (Exception e) {
            log.error("Error getting all attempts", e);
            return ResponseEntity.ok(List.of());
        }
    }
}
