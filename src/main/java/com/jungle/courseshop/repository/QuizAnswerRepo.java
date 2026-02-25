package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepo extends JpaRepository<QuizAnswer, Long> {
    
    // Lấy tất cả đáp án của câu hỏi
    List<QuizAnswer> findByQuestionIdOrderByOrderIndexAsc(Long questionId);
    
    // Lấy đáp án đúng của câu hỏi
    List<QuizAnswer> findByQuestionIdAndIsCorrectTrue(Long questionId);
    
    // Xóa tất cả đáp án của câu hỏi
    void deleteByQuestionId(Long questionId);
}
