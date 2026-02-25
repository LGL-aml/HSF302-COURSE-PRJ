package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptAnswerRepo extends JpaRepository<QuizAttemptAnswer, Long> {
    
    // Lấy tất cả câu trả lời trong một lần làm bài
    List<QuizAttemptAnswer> findByAttemptId(Long attemptId);
    
    // Lấy câu trả lời của một câu hỏi cụ thể trong lần làm bài
    QuizAttemptAnswer findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
    
    // Xóa tất cả câu trả lời của attempt
    void deleteByAttemptId(Long attemptId);
}
