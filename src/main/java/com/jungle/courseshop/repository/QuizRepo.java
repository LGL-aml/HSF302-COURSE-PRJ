package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, Long> {
    
    // Tìm quiz theo course
    List<Quiz> findByCourseIdAndActiveTrue(Long courseId);
    
    // Tìm quiz theo module
    List<Quiz> findByModuleIdAndActiveTrue(Long moduleId);
    
    // Tìm tất cả quiz của course (bao gồm cả module)
    List<Quiz> findByCourseId(Long courseId);
    
    // Tìm quiz active
    List<Quiz> findByActiveTrue();
    
    // Tìm quiz theo title
    Optional<Quiz> findByTitleAndCourseId(String title, Long courseId);
}
