package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepo extends JpaRepository<QuizQuestion, Long> {
    
    // Lấy tất cả câu hỏi của quiz, sắp xếp theo thứ tự
    List<QuizQuestion> findByQuizIdOrderByOrderIndexAsc(Long quizId);
    
    // Đếm số câu hỏi trong quiz
    Long countByQuizId(Long quizId);
    
    // Xóa tất cả câu hỏi của quiz
    void deleteByQuizId(Long quizId);
}
