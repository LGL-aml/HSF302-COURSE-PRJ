package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Quiz;
import com.jungle.courseshop.entity.QuizAttempt;
import com.jungle.courseshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepo extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserAndQuizOrderByCompletedAtDesc(User user, Quiz quiz);
    Optional<QuizAttempt> findTopByUserAndQuizOrderByCompletedAtDesc(User user, Quiz quiz);
    boolean existsByUserAndQuizAndPassedTrue(User user, Quiz quiz);
    long countByUserAndQuiz(User user, Quiz quiz);
}
