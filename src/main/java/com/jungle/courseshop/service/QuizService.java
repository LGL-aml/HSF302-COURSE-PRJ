package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.request.QuizCreateRequest;
import com.jungle.courseshop.dto.request.QuizSubmitRequest;
import com.jungle.courseshop.dto.response.QuizAttemptResponse;
import com.jungle.courseshop.dto.response.QuizDetailResponse;
import com.jungle.courseshop.dto.response.QuizResponse;

import java.util.List;

public interface QuizService {
    
    // CRUD operations
    QuizResponse createQuiz(QuizCreateRequest request);
    
    QuizResponse updateQuiz(Long id, QuizCreateRequest request);
    
    void deleteQuiz(Long id);
    
    QuizResponse getQuizById(Long id);
    
    QuizDetailResponse getQuizDetail(Long id);
    
    // Get quizzes
    List<QuizResponse> getQuizzesByCourse(Long courseId);
    
    List<QuizResponse> getQuizzesByModule(Long moduleId);
    
    List<QuizResponse> getMyQuizzes(); // For lecturers
    
    // Quiz attempts
    QuizAttemptResponse startQuizAttempt(Long quizId);
    
    QuizAttemptResponse submitQuiz(QuizSubmitRequest request);
    
    QuizAttemptResponse getAttemptDetail(Long attemptId);
    
    List<QuizAttemptResponse> getMyAttempts(Long quizId);
    
    List<QuizAttemptResponse> getAllAttemptsForQuiz(Long quizId); // For lecturers
    
    // Statistics
    boolean hasUserPassedQuiz(Long quizId, Long userId);
    
    Double getUserBestScore(Long quizId, Long userId);
    
    Integer getUserAttemptCount(Long quizId, Long userId);
}
