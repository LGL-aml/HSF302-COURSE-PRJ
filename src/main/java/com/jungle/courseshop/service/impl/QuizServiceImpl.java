package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.dto.request.QuizSubmitRequest;
import com.jungle.courseshop.dto.response.QuizResponse;
import com.jungle.courseshop.dto.response.QuizResultResponse;
import com.jungle.courseshop.entity.*;
import com.jungle.courseshop.exception.ResourceNotFoundException;
import com.jungle.courseshop.repository.*;
import com.jungle.courseshop.service.CourseEnrollmentService;
import com.jungle.courseshop.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepo quizRepo;
    private final QuizQuestionRepo questionRepo;
    private final QuizAttemptRepo attemptRepo;
    private final UserRepo userRepo;
    private final CourseEnrollmentService enrollmentService;

    @Override
    public QuizResponse getQuizForStudent(Long moduleId) {
        Quiz quiz = quizRepo.findByCourseModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz không tồn tại cho module này"));

        return mapToResponse(quiz, false);
    }

    @Override
    public QuizResponse getQuizForLecturer(Long moduleId) {
        Quiz quiz = quizRepo.findByCourseModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz không tồn tại cho module này"));

        return mapToResponse(quiz, true);
    }

    @Override
    @Transactional
    public QuizResultResponse submitQuiz(QuizSubmitRequest request) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepo.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz không tồn tại"));

        List<QuizQuestion> questions = questionRepo.findByQuizIdOrderByOrderIndexAsc(quiz.getId());
        Map<Long, Long> answers = request.getAnswers(); // questionId -> selectedOptionId

        int totalQuestions = questions.size();
        int correctAnswers = 0;
        List<QuizResultResponse.QuestionResult> questionResults = new ArrayList<>();

        for (QuizQuestion question : questions) {
            Long selectedOptionId = answers.get(question.getId());
            QuizOption correctOption = question.getOptions().stream()
                    .filter(QuizOption::getIsCorrect)
                    .findFirst()
                    .orElse(null);

            boolean isCorrect = false;
            if (selectedOptionId != null && correctOption != null) {
                isCorrect = selectedOptionId.equals(correctOption.getId());
            }
            if (isCorrect) correctAnswers++;

            // Build option results
            List<QuizResultResponse.OptionResult> optionResults = question.getOptions().stream()
                    .map(opt -> QuizResultResponse.OptionResult.builder()
                            .id(opt.getId())
                            .optionText(opt.getOptionText())
                            .isCorrect(opt.getIsCorrect())
                            .isSelected(opt.getId().equals(selectedOptionId))
                            .build())
                    .collect(Collectors.toList());

            questionResults.add(QuizResultResponse.QuestionResult.builder()
                    .questionId(question.getId())
                    .questionText(question.getQuestionText())
                    .selectedOptionId(selectedOptionId)
                    .correctOptionId(correctOption != null ? correctOption.getId() : null)
                    .isCorrect(isCorrect)
                    .explanation(correctOption != null ? correctOption.getExplanation() : null)
                    .options(optionResults)
                    .build());
        }

        double score = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0;
        boolean passed = score >= quiz.getPassScore();

        // Lưu kết quả
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(currentUser);
        attempt.setQuiz(quiz);
        attempt.setScore(score);
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setPassed(passed);
        attemptRepo.save(attempt);

        long attemptCount = attemptRepo.countByUserAndQuiz(currentUser, quiz);

        // Cập nhật progress khóa học sau khi làm quiz
        if (passed) {
            try {
                Long courseId = quiz.getCourseModule().getCourse().getId();
                enrollmentService.recalculateProgress(courseId);
            } catch (Exception e) {
                log.warn("Không thể cập nhật progress sau khi submit quiz: {}", e.getMessage());
            }
        }

        return QuizResultResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .score(Math.round(score * 100.0) / 100.0)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .passed(passed)
                .passScore(quiz.getPassScore())
                .completedAt(attempt.getCompletedAt())
                .attemptCount(attemptCount)
                .questionResults(questionResults)
                .build();
    }

    @Override
    public boolean hasPassedQuiz(Long moduleId) {
        User currentUser = getCurrentUser();
        Quiz quiz = quizRepo.findByCourseModuleId(moduleId).orElse(null);
        if (quiz == null) return true; // Nếu không có quiz thì coi như pass
        return attemptRepo.existsByUserAndQuizAndPassedTrue(currentUser, quiz);
    }

    // ====== Helper methods ======

    private QuizResponse mapToResponse(Quiz quiz, boolean showAnswers) {
        List<QuizResponse.QuestionResponse> questionResponses = quiz.getQuestions().stream()
                .map(q -> QuizResponse.QuestionResponse.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .orderIndex(q.getOrderIndex())
                        .options(q.getOptions().stream()
                                .map(opt -> QuizResponse.OptionResponse.builder()
                                        .id(opt.getId())
                                        .optionText(opt.getOptionText())
                                        .isCorrect(showAnswers ? opt.getIsCorrect() : null)
                                        .explanation(showAnswers ? opt.getExplanation() : null)
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .passScore(quiz.getPassScore())
                .moduleId(quiz.getCourseModule().getId())
                .moduleName(quiz.getCourseModule().getTitle())
                .questions(questionResponses)
                .active(quiz.getActive())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepo.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
