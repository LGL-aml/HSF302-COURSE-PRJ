package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepo extends JpaRepository<QuizAttempt, Long> {
    
    // Lấy tất cả lần làm bài của user cho quiz
    List<QuizAttempt> findByQuizIdAndUserIdOrderByAttemptNumberDesc(Long quizId, Long userId);
    
    // Lấy lần làm bài mới nhất của user
    Optional<QuizAttempt> findFirstByQuizIdAndUserIdOrderByAttemptNumberDesc(Long quizId, Long userId);
    
    // Đếm số lần user đã làm quiz
    Long countByQuizIdAndUserId(Long quizId, Long userId);
    
    // Lấy điểm cao nhất của user trong quiz
    @Query("SELECT MAX(qa.score) FROM QuizAttempt qa WHERE qa.quiz.id = ?1 AND qa.user.id = ?2")
    Double findMaxScoreByQuizIdAndUserId(Long quizId, Long userId);
    
    // Lấy tất cả attempts của user
    List<QuizAttempt> findByUserIdOrderByStartedAtDesc(Long userId);
    
    // Lấy tất cả attempts của quiz
    List<QuizAttempt> findByQuizIdOrderByStartedAtDesc(Long quizId);
    
    // Kiểm tra user đã pass quiz chưa
    boolean existsByQuizIdAndUserIdAndPassedTrue(Long quizId, Long userId);
}
